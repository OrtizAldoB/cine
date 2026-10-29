$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

$out = Join-Path $PSScriptRoot "out"
New-Item -ItemType Directory -Force -Path $out | Out-Null

$sources = Get-ChildItem -Path (Join-Path $PSScriptRoot "src\main\java\com\cine") -Filter *.java |
    Select-Object -ExpandProperty FullName

& javac -encoding UTF-8 -d $out $sources
if ($LASTEXITCODE -ne 0) {
    throw "Error al compilar el proyecto."
}

$port = if ($args.Count -gt 0) { $args[0] } else { "8080" }
& java -cp $out com.cine.CineServer $port
