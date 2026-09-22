param(
    [string]$GodotExe = $env:ROMGAME_GODOT
)

$ErrorActionPreference = "Stop"
$repo = Split-Path -Parent $PSScriptRoot
$project = Join-Path $repo "app/src/main/assets"

if (-not $GodotExe) {
    $GodotExe = Get-ChildItem (Join-Path $repo ".tools/godot") -Filter "Godot*_console.exe" -ErrorAction SilentlyContinue |
        Select-Object -First 1 -ExpandProperty FullName
}
if (-not $GodotExe -or -not (Test-Path -LiteralPath $GodotExe)) {
    throw "Define ROMGAME_GODOT o coloca Godot 4.7.2 portátil en .tools/godot."
}

$generated = @(
    "project.binary",
    "main.tscn.remap",
    "scripts/main.gdc",
    "scripts/main.gd.remap"
)
foreach ($relative in $generated) {
    $target = Join-Path $project $relative
    if (Test-Path -LiteralPath $target) { Remove-Item -LiteralPath $target -Force }
}
Get-ChildItem (Join-Path $project "scripts") -File |
    Where-Object { $_.Extension -in ".gdc", ".remap" } |
    Remove-Item -Force
Get-ChildItem $project -File -Filter "*.remap" | Remove-Item -Force
$exported = Join-Path $project ".godot/exported"
if (Test-Path -LiteralPath $exported) { Remove-Item -LiteralPath $exported -Recurse -Force }

$pack = Join-Path $project "arcade.zip"
& $GodotExe --headless --path $project --export-pack "Android Pack" $pack
if ($LASTEXITCODE -ne 0) { throw "Godot no pudo exportar el pack." }
tar -xf $pack -C $project
Remove-Item -LiteralPath $pack -Force
Write-Output "Godot Arcade exportado en app/src/main/assets."
