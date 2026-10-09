import sys
from fontTools.ttLib import TTFont, newTable
from fontTools.pens.cu2quPen import Cu2QuPen
from fontTools.pens.ttGlyphPen import TTGlyphPen

src, dst = sys.argv[1], sys.argv[2]
font = TTFont(src)
font.flavor = None
order = font.getGlyphOrder()
gs = font.getGlyphSet()
glyphs = {}
for name in order:
    pen = TTGlyphPen(None)
    gs[name].draw(Cu2QuPen(pen, max_err=1.0, reverse_direction=True))
    glyphs[name] = pen.glyph()
glyf = newTable('glyf'); glyf.glyphOrder = order; glyf.glyphs = glyphs
font['glyf'] = glyf
loca = newTable('loca'); font['loca'] = loca
font['maxp'] = maxp = newTable('maxp')
maxp.tableVersion = 0x00010000
for attr in ('maxZones', 'maxTwilightPoints', 'maxStorage', 'maxFunctionDefs', 'maxInstructionDefs', 'maxStackElements', 'maxSizeOfInstructions', 'maxComponentElements'):
    setattr(maxp, attr, 0)
maxp.maxZones = 1
maxp.maxComponentDepth = 0
font['head'].glyphDataFormat = 0
font['post'].formatType = 2.0
font['post'].extraNames = []
font['post'].mapping = {}
font['post'].glyphOrder = order
for t in ('CFF ', 'VORG', 'DYNA', 'GDYN'):
    if t in font:
        del font[t]
font.sfntVersion = '\x00\x01\x00\x00'
font.save(dst)
chk = TTFont(dst)
print(dst, 'glyf' in chk, 'CFF ' in chk, chk.sfntVersion == '\x00\x01\x00\x00', len(chk.getGlyphOrder()))
