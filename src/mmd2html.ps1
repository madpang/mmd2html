#! /usr/bin/env pwsh -NoProfile
<#
	@brief: Convert custom plain text markup to HTML.

	@details: Just wrap the input text in <pre> tag

	@author
	- madpang

	@date:
	- Created on 2025-05-17
	- Updated on 2025-05-21
#>

param(
	[Parameter(Mandatory = $True, Position = 1)][string]$path2txt
)

# === Read the input text
$in_lines_ = Get-Content -Path $path2txt

# === Initialize the output HTML formatted lines
$out_lines_ = @()

# === Processing
if ($in_lines_.Count -eq 0) {
	Write-Error "Input file is empty"
	exit 1
}
if ($in_lines_[0] -notmatch '^\+{3} header$') {
	Write-Error "Every post must provide header info."
	exit 2
}
# --- Parse the header
# Track the processed line number
$ln_track_ = 1
# Initialize a hashtable to hold header meta info
$meta_info_ = @{ title = $null }
for ($ln = 1; $ln -lt $in_lines_.Count; $ln++) {
	$line = $in_lines_[$ln]
	if ($line -match '^\+{3}$') {
		# Header ends
		$ln_track_ = $ln
		break
	}
	if ($line -match '^@title:\s*(.*)$') {
		$meta_info_.title = $Matches[1].Trim()
	}
}
if (-not $meta_info_.title) {
	Write-Error "MISSING META INFO: @title"
	exit 3
}
# --- Parse the content
$out_lines_ += "<h1>$($meta_info_.title)</h1>"
$out_lines_ += "<pre>"
for ($ln = $ln_track_+1; $ln -lt $in_lines_.Count; $ln++) {
	$line = $in_lines_[$ln]
	$out_lines_ += $line
}
$out_lines_ += "</pre>"

# === Output the HTML formatted lines
Write-Output $out_lines_
exit 0
