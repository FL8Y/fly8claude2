# Gen 3 text codec (English charmap subset)
import re
CH = {0x00:' ',0xAB:'!',0xAC:'?',0xAD:'.',0xAE:'-',0xB0:'…',0xB1:'“',0xB2:'”',0xB3:'‘',0xB4:'’',0xB5:'♂',0xB6:'♀',0xB7:'$',0xB8:',',0xB9:'×',0xBA:'/',
      0x1B:'é',0x2D:'&',0x5A:'Í',0x5C:'(',0x5D:')',0x35:'=',0x36:';',0x51:'¿',0x52:'¡',0xF0:':',0x34:'Lv',0x53:'PK',0x54:'MN',0x55:'PO',0x56:'Ké',0x5B:'%'}
for i in range(10): CH[0xA1+i]=str(i)
for i in range(26): CH[0xBB+i]=chr(65+i); CH[0xD5+i]=chr(97+i)
REV = {v:k for k,v in CH.items() if len(v)==1}
def enc(s): return bytes(REV[c] for c in s)
def dec(b, i, maxlen=4000, ctrl=True):
    out=[]; j=i
    while j < len(b) and j-i < maxlen:
        c=b[j]
        if c==0xFF: return ''.join(out), j+1
        if c==0xFE: out.append('\n')
        elif c==0xFA: out.append('\n')
        elif c==0xFB: out.append('\n\n')
        elif c==0xFD:
            j+=1; v=b[j]; out.append({1:'{PLAYER}',2:'{STR1}',3:'{STR2}',4:'{STR3}',6:'{RIVAL}',7:'{VERSION}',8:'{AQUA}',9:'{MAGMA}',10:'{ARCHIE}',11:'{MAXIE}',12:'{KYOGRE}',13:'{GROUDON}'}.get(v,'{B%d}'%v))
        elif c==0xFC:
            j+=1; v=b[j]
            skip={1:1,2:1,3:1,4:3,5:1,6:1,8:1,0x10:2,0x11:1,0x12:1,0x13:1,0x14:1,0x15:0,0x16:0,0x17:0,0x18:0,0x0B:2,0x0C:1,0x0D:0,0x0E:1,0x0F:0,0x09:0,0x0A:0,0x07:0}.get(v,0)
            j+=skip
        elif c in CH: out.append(CH[c])
        else: return None, j
        j+=1
    return None, j
