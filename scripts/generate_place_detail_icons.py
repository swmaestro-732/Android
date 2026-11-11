#!/usr/bin/env python3
"""확정 HTML의 SVG를 24dp Android VectorDrawable로 변환한다 (외부 라이브러리 없음)."""
import re
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / 'docs/ui/place-detail-icons/approved.html'
TARGET = ROOT / 'common/presentation/src/main/res/drawable'
ANDROID = 'http://schemas.android.com/apk/res/android'
ET.register_namespace('android', ANDROID)


def attrs(**values):
    return {f'{{{ANDROID}}}{key}': str(value) for key, value in values.items()}


def outline(element):
    a = element.attrib
    if element.tag == 'path':
        return a['d']
    if element.tag in ('circle', 'ellipse'):
        x, y = float(a['cx']), float(a['cy'])
        rx = float(a.get('rx', a.get('r', '0')))
        ry = float(a.get('ry', a.get('r', '0')))
        return f'M{x-rx:g},{y:g}a{rx:g},{ry:g} 0 1,0 {2*rx:g},0a{rx:g},{ry:g} 0 1,0 {-2*rx:g},0Z'
    if element.tag == 'rect':
        x, y, w, h = (float(a.get(key, '0')) for key in ('x', 'y', 'width', 'height'))
        rx = min(float(a.get('rx', a.get('ry', '0'))), w / 2)
        ry = min(float(a.get('ry', a.get('rx', '0'))), h / 2)
        if rx == 0 or ry == 0:
            return f'M{x:g},{y:g}h{w:g}v{h:g}h{-w:g}Z'
        return (f'M{x+rx:g},{y:g}h{w-2*rx:g}a{rx:g},{ry:g} 0 0,1 {rx:g},{ry:g}'
                f'v{h-2*ry:g}a{rx:g},{ry:g} 0 0,1 {-rx:g},{ry:g}'
                f'h{2*rx-w:g}a{rx:g},{ry:g} 0 0,1 {-rx:g},{-ry:g}'
                f'v{2*ry-h:g}a{rx:g},{ry:g} 0 0,1 {rx:g},{-ry:g}Z')
    raise ValueError(f'Unsupported SVG element: {element.tag}')


def append_element(parent, element):
    transform = element.get('transform')
    if transform:
        match = re.fullmatch(r'rotate\(([-\d.]+)[ ,]+([-\d.]+)[ ,]+([-\d.]+)\)', transform)
        if not match:
            raise ValueError(f'Unsupported transform: {transform}')
        parent = ET.SubElement(parent, 'group', attrs(rotation=match[1], pivotX=match[2], pivotY=match[3]))
    if element.tag == 'g':
        for child in element:
            append_element(parent, child)
        return
    filled = element.get('class') == 'dot'
    attributes = attrs(pathData=outline(element), fillColor='@android:color/black' if filled else '@android:color/transparent')
    if not filled:
        attributes.update(attrs(strokeColor='@android:color/black', strokeWidth=element.get('stroke-width', '1.65'),
                                strokeLineCap='round', strokeLineJoin='round'))
    ET.SubElement(parent, 'path', attributes)


def main():
    html = SOURCE.read_text(encoding='utf-8')
    matches = re.findall(r'(<svg class="ic".*?</svg>).*?<div class="code">([^<]+)</div>', html, re.S)
    assert len(matches) == 64 and len({code for _, code in matches}) == 64
    for source, code in matches:
        svg = ET.fromstring(source)
        assert svg.get('viewBox') == '0 0 24 24'
        vector = ET.Element('vector', attrs(width='24dp', height='24dp', viewportWidth=24, viewportHeight=24))
        for element in svg:
            append_element(vector, element)
        ET.indent(vector, space='    ')
        content = '<?xml version="1.0" encoding="utf-8"?>\n' + ET.tostring(vector, encoding='unicode') + '\n'
        (TARGET / f'ic_place_detail_{code}_24.xml').write_text(content, encoding='utf-8')
    print(f'Generated {len(matches)} vector drawables from {SOURCE.relative_to(ROOT)}')


if __name__ == '__main__':
    main()
