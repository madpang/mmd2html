#! /usr/bin/env pwsh -NoProfile
<#
	@brief: Convert custom plain text markup to HTML.

    @details: Just wrap the input text in <pre> tag

	@author
	- madpang

	@date:
	- Created on 2025-05-17
	- Updated on 2025-05-17
#>

param(
	[Parameter(Mandatory = $True, Position = 1)][string]$path2txt
)

# === Read the input text
$in_lines = Get-Content -Path $path2txt

# === Initialize the output HTML formatted lines
$out_lines = @()

# === Processing
$out_lines += "<pre>"
for ($ii = 0; $ii -lt $in_lines.Count; $ii++) {
    $line = $in_lines[$ii]
    $out_lines += $line
}
$out_lines += "</pre>"

# === Output the HTML formatted lines
Write-Output $out_lines
