#!/usr/bin/env python3
"""Build bundled sido/sigungu map data from admdongkor and downloaded legal-area codes.

Requires Shapely 2.x in a tooling virtualenv, not an Android dependency.
See docs/data/area-map.md for the pinned source and attribution.
"""

import argparse
from collections import defaultdict
import json
from pathlib import Path

from shapely.geometry import Polygon, MultiPolygon, shape
from shapely.geometry.polygon import orient
from shapely.ops import unary_union

from generate_legal_areas import map_names, read_legal_areas


def generate(source: Path, legal_codes: Path, output: Path, tolerance: float) -> None:
    names = map_names(read_legal_areas(legal_codes))

    groups = defaultdict(list)
    for feature in json.loads(source.read_text(encoding="utf-8"))["features"]:
        properties = feature["properties"]
        geometry = shape(feature["geometry"])
        if not geometry.is_valid or not isinstance(geometry, (Polygon, MultiPolygon)):
            raise ValueError(f"Invalid source geometry: {properties}")
        # adm_cd is a statistics code, adm_cd2 is an administrative-dong code.
        # Only the verified sido/sgg prefixes match the downloaded legal-area codes.
        for code in (properties["sido"], properties["sgg"]):
            if code in names:
                source_name = properties["sidonm"] if len(code) == 2 else properties["sggnm"]
                if source_name.replace(" ", "") != names[code][0].replace(" ", ""):
                    raise ValueError(f"Boundary/legal-code name mismatch: {code}: {source_name}")
                groups[code].append(geometry)
            elif code != "36110":  # Sejong has no intervening sigungu in the legal-code source.
                raise ValueError(f"Boundary code absent from legal-code source: {code}")

    if set(groups) != set(names):
        raise ValueError(f"Legal regions without geometry: {set(names) - set(groups)}")

    def ring(coordinates):
        return [[round(lat, 6), round(lng, 6)] for lng, lat in coordinates]

    result = []
    for code, geometries in sorted(groups.items()):
        merged = unary_union(geometries).simplify(tolerance, preserve_topology=True)
        if not isinstance(merged, (Polygon, MultiPolygon)):
            raise ValueError(f"Non-polygon union: {code}")
        polygons = [merged] if isinstance(merged, Polygon) else list(merged.geoms)
        # No area threshold: small islands and holes are retained.
        parts = [
            {"outer": ring(p.exterior.coords), "holes": [ring(h.coords) for h in p.interiors]}
            for p in polygons
        ]
        for part in parts:
            reconstructed = Polygon(part["outer"], part["holes"])
            if not reconstructed.is_valid or reconstructed.area == 0:
                raise ValueError(f"Rounding damaged geometry: {code}")
            # Keep holes opposite to their exterior for polygon renderers.
            # Orient after rounding and swapping to latitude/longitude.
            oriented = orient(reconstructed, sign=1.0)
            part["outer"] = list(oriented.exterior.coords)
            part["holes"] = [list(h.coords) for h in oriented.interiors]
        name, full_name = names[code]
        result.append({
            "code": code,
            "name": name,
            "fullName": full_name,
            "level": "SIDO" if len(code) == 2 else "SIGUNGU",
            "polygons": parts,
        })

    output.parent.mkdir(parents=True, exist_ok=True)
    output.write_text(json.dumps(result, ensure_ascii=False, separators=(",", ":")) + "\n", encoding="utf-8")
    print(f"{len(result)} regions, {output.stat().st_size} bytes -> {output}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("source", type=Path, help="Downloaded admdongkor GeoJSON")
    parser.add_argument("legal_codes", type=Path, help="Downloaded 법정동코드 전체자료.txt")
    parser.add_argument("output", type=Path, help="Output area_map.json")
    parser.add_argument("--tolerance", type=float, default=0.002, help="Simplification in degrees")
    args = parser.parse_args()
    if args.tolerance < 0:
        parser.error("tolerance must be nonnegative")
    generate(args.source, args.legal_codes, args.output, args.tolerance)
