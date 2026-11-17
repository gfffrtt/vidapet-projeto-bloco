"""Converte os .md de docs/ em um único HTML (docs/gerar_pdf/relatorio.html) para imprimir em PDF.
Suporta apenas o Markdown usado nestes documentos: títulos, tabelas, listas, negrito, itálico,
código inline, imagem e régua. Uso: python3 docs/gerar_pdf/md2html.py
"""
import html
import os
import re

AQUI = os.path.dirname(os.path.abspath(__file__))
DOCS = os.path.dirname(AQUI)
ARQUIVOS = ["01-requisitos.md", "02-casos-de-uso.md", "03-justificativa-escopo.md"]
PULAR = ("**Aluno:**", "**Disciplina:**", "**Trilha")


def inline(t):
    t = html.escape(t, quote=False)
    t = re.sub(r"!\[([^\]]*)\]\(([^)]+)\)", r'<img alt="\1" src="../\2">', t)
    t = re.sub(r"`([^`]+)`", r"<code>\1</code>", t)
    t = re.sub(r"\*\*([^*]+)\*\*", r"<b>\1</b>", t)
    t = re.sub(r"(?<![\w*])\*([^*]+)\*(?![\w*])", r"<i>\1</i>", t)
    return t


def celulas(linha):
    return [c.strip() for c in linha.strip().strip("|").split("|")]


def converter(texto):
    linhas = [l for l in texto.splitlines() if not l.startswith(PULAR)]
    out, i = [], 0
    while i < len(linhas):
        l = linhas[i]
        if l.startswith("#"):
            n = len(l) - len(l.lstrip("#"))
            out.append(f"<h{n}>{inline(l[n:].strip())}</h{n}>")
        elif l.startswith("|") and i + 1 < len(linhas) and re.match(r"^\|[\s\-|:]+\|$", linhas[i + 1]):
            out.append("<table><tr>" + "".join(f"<th>{inline(c)}</th>" for c in celulas(l)) + "</tr>")
            i += 2
            while i < len(linhas) and linhas[i].startswith("|"):
                out.append("<tr>" + "".join(f"<td>{inline(c)}</td>" for c in celulas(linhas[i])) + "</tr>")
                i += 1
            out.append("</table>")
            continue
        elif re.match(r"^(- |\d+\. )", l):
            tag = "ul" if l.startswith("- ") else "ol"
            out.append(f"<{tag}>")
            while i < len(linhas) and re.match(r"^(- |\d+\. )", linhas[i]):
                out.append("<li>" + inline(re.sub(r"^(- |\d+\. )", "", linhas[i])) + "</li>")
                i += 1
            out.append(f"</{tag}>")
            continue
        elif l.strip() == "---":
            out.append("<hr>")
        elif l.strip():
            out.append(f"<p>{inline(l)}</p>")
        i += 1
    return "\n".join(out)


CSS = """
@page{size:A4;margin:18mm 16mm}
body{font-family:Helvetica,Arial,sans-serif;font-size:10.5pt;line-height:1.4;color:#222}
h1{font-size:17pt;border-bottom:2px solid #2b6cb0;padding-bottom:4px;page-break-before:always;color:#1a365d}
h2{font-size:13pt;color:#2b6cb0;margin-top:18px}
table{border-collapse:collapse;width:100%;margin:8px 0;font-size:9.5pt}
tr{page-break-inside:avoid}
th,td{border:1px solid #bbb;padding:4px 6px;vertical-align:top;text-align:left}
th{background:#edf2f7}
code{background:#f1f1f1;padding:1px 3px;border-radius:3px;font-size:9pt}
img{max-width:100%}
.capa{text-align:center;padding-top:170px}
.capa h1{border:none;page-break-before:avoid;font-size:24pt}
.capa p{font-size:13pt;margin:6px}
hr{border:none;border-top:1px solid #ddd}
"""

CAPA = """<div class="capa"><h1>Sistema de Agendamento da Clínica Veterinária VidaPet</h1>
<p><b>TP1 – Requisitos, Casos de Uso e Programa Standalone Inicial</b></p><br>
<p>Aluno: <b>Gustavo Trapp</b></p><p>Disciplina: <b>Projeto de Bloco (PB)</b></p>
<p>Trilha: Java 21 (LTS) + Spring Boot · IntelliJ IDEA Community</p><br>
<p>Repositório GitHub: <a href="https://github.com/gfffrtt/vidapet-projeto-bloco">github.com/gfffrtt/vidapet-projeto-bloco</a></p><br><p>Novembro de 2026</p></div>"""

corpo = []
for nome in ARQUIVOS:
    with open(os.path.join(DOCS, nome), encoding="utf-8") as f:
        corpo.append(converter(f.read()))

with open(os.path.join(AQUI, "relatorio.html"), "w", encoding="utf-8") as f:
    f.write(f'<!doctype html><html lang="pt-br"><head><meta charset="utf-8"><title>TP1 VidaPet</title>'
            f"<style>{CSS}</style></head><body>{CAPA}\n" + "\n".join(corpo) + "</body></html>")
print("ok:", os.path.join(AQUI, "relatorio.html"))
