#!/usr/bin/env python3
"""Build active legal eup/myeon/dong codes from the downloaded official TSV.

The source includes province/city parent rows, abolished codes, and legal ri.
Only active 10-digit eup/myeon/dong rows match the server's existing area master.
"""

import argparse
import csv
import io
import json
from pathlib import Path


def read_legal_areas(source: Path) -> list[dict[str, str]]:
    raw = source.read_bytes()
    try:
        content = raw.decode("utf-8-sig")
    except UnicodeDecodeError:
        content = raw.decode("cp949")
    rows = csv.DictReader(io.StringIO(content), delimiter="\t")
    if rows.fieldnames != ["법정동코드", "법정동명", "폐지여부"]:
        raise ValueError("Expected official legal-area TSV columns")
    result = {}
    for row in rows:
        code, name, status = (row[key].strip() for key in rows.fieldnames)
        if len(code) != 10 or not code.isascii() or not code.isdigit():
            raise ValueError(f"Invalid legal code: {code}")
        if status not in ("존재", "폐지"):
            raise ValueError(f"Unknown legal-area status: {status}")
        # Parent rows end in 00000; ri rows have a nonzero last two digits.
        if status != "존재" or not code.endswith("00") or code.endswith("00000"):
            continue
        if code in result or len(name.split()) < 2:
            raise ValueError(f"Duplicate code or invalid full name: {code}: {name}")
        result[code] = {"code": code, "name": name.split()[-1], "fullName": name, "level": "DONG"}
    if not result:
        raise ValueError("No active legal eup/myeon/dong codes")
    return [result[code] for code in sorted(result)]


def map_names(areas: list[dict[str, str]]) -> dict[str, tuple[str, str]]:
    """Derive province/district names from active legal-area parent names."""
    result = {}
    for area in areas:
        code = area["code"]
        province, remainder = area["fullName"].split(" ", 1)
        pairs = [(code[:2], (province, province))]
        # Sejong's eup/myeon/dong have no intervening city/county/district.
        if " " in remainder:
            district = remainder.rsplit(" ", 1)[0]
            pairs.append((code[:5], (district, f"{province} {district}")))
        for prefix, name in pairs:
            if prefix in result and result[prefix] != name:
                raise ValueError(f"Inconsistent legal parent name: {prefix}")
            result[prefix] = name
    return result


def generate(source: Path, output: Path) -> None:
    areas = read_legal_areas(source)
    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(areas, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"{len(areas)} active legal areas, {output.stat().st_size} bytes -> {output}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("source", type=Path, help="Downloaded 법정동코드 전체자료.txt (CP949 or UTF-8)")
    parser.add_argument("output", type=Path, help="Output legal_areas.json")
    args = parser.parse_args()
    generate(args.source, args.output)
