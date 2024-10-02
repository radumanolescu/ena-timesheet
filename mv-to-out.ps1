# move the files to the output folder
# overwrite the files if they already exist
$files = Get-ChildItem -Path \Users\Radu\Downloads\ena_dropdown.txt
foreach ($file in $files) {
    Move-Item -Path $file.FullName -Destination \\HIPPIE\SharedByRadu\-\ena-out -Force
}

$files = Get-ChildItem -Path "\Users\Radu\Downloads\PHD timesheet *.xlsx"
foreach ($file in $files) {
    Move-Item -Path $file.FullName -Destination \\HIPPIE\SharedByRadu\-\ena-out -Force
}