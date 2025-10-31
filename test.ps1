# count_loc.ps1

# Function to count lines in a folder recursively, ignoring build/generated folders
function Count-Lines ($path, $extensions) {
    $files = Get-ChildItem -Path $path -Recurse -File | Where-Object {
        ($_.Extension -in $extensions) -and
        ($_.FullName -notmatch "\\build\\")   # skip build folders
    }
    $lines = 0
    foreach ($file in $files) {
        try {
            $lines += (Get-Content $file.FullName | Measure-Object -Line).Lines
        } catch {
            Write-Host "Skipping $($file.FullName) due to read error"
        }
    }
    return $lines
}

# Count Java/Kotlin
$javaLines = Count-Lines ".\app\src" ".java", ".kt"
Write-Host "Total Java/Kotlin lines: $javaLines"

# Count Layout XMLs
$layoutLines = Count-Lines ".\app\src\main\res\layout" ".xml"
Write-Host "Total layout XML lines: $layoutLines"


# Total LOC
$totalLines = $javaLines + $layoutLines
Write-Host "`nTotal LOC (Java/Kotlin + XML): $totalLines"

Read-Host "`nPress Enter to exit"