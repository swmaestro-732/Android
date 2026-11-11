"""Guard supported locales against missing resources and incompatible format arguments."""
from pathlib import Path
import re
import json
import unittest
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
PLACEHOLDER = re.compile(r"%(?:(\d+)\$)?[-#+ 0,(]*\d*(?:\.\d+)?([a-zA-Z])")


def resources(directory):
    result = {}
    for path in directory.glob('*.xml'):
        for item in ET.parse(path).getroot():
            if item.tag in ('string', 'plurals', 'string-array') and item.get('translatable') != 'false':
                name = item.attrib['name']
                if name in result:
                    raise AssertionError(f'Duplicate resource: {directory}/{name}')
                result[name] = item
    return result


def formats(text):
    return sorted(PLACEHOLDER.findall(text or ''))


class StringResourcesTest(unittest.TestCase):
    def test_bundled_districts_have_localized_labels(self):
        areas = json.loads((ROOT / 'main/data/src/main/assets/area_map.json').read_text())
        codes = {item['code'] for item in areas if item['level'] == 'SIGUNGU'}
        expected = {'district_' + code for code in codes}
        for locale in ('values', 'values-en', 'values-ja'):
            path = ROOT / 'main/presentation/src/main/res' / locale / 'district_names.xml'
            labels = {item.attrib['name']: item.text for item in ET.parse(path).getroot()}
            with self.subTest(locale=locale):
                self.assertEqual(expected, set(labels))
                self.assertTrue(all(value and value.strip() for value in labels.values()))
        helper = (ROOT / 'main/presentation/src/main/java/com/chillsam/courmy/main/'
                  'presentation/home/DistrictLabels.kt').read_text()
        pairs = re.findall(r'"(\d+)" to R.string.district_(\d+)', helper)
        self.assertEqual({(code, code) for code in codes}, set(pairs))
        self.assertEqual(len(codes), len(pairs))

    def test_supported_locales_have_matching_formats(self):
        for module in ('common', 'course', 'main'):
            root = ROOT / module / 'presentation/src/main/res'
            source = resources(root / 'values')
            for locale in ('en', 'ja'):
                target = resources(root / f'values-{locale}')
                with self.subTest(module=module, locale=locale):
                    self.assertEqual(set(source), set(target))
                for name, original in source.items():
                    translated = target[name]
                    with self.subTest(module=module, locale=locale, name=name):
                        self.assertEqual(original.tag, translated.tag)
                        if original.tag == 'string':
                            self.assertEqual(formats(''.join(original.itertext())),
                                             formats(''.join(translated.itertext())))
                        elif original.tag == 'string-array':
                            self.assertEqual(len(original), len(translated))
                            for before, after in zip(original, translated):
                                self.assertTrue(''.join(after.itertext()).strip())
                                self.assertEqual(formats(''.join(before.itertext())),
                                                 formats(''.join(after.itertext())))
                        else:
                            fallback = original.find("item[@quantity='other']")
                            self.assertIsNotNone(fallback)
                            self.assertIsNotNone(translated.find("item[@quantity='other']"))
                            for variant in translated:
                                self.assertEqual(formats(fallback.text), formats(variant.text))


if __name__ == '__main__':
    unittest.main()
