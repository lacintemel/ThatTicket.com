# Create fonts directory if it doesn't exist
New-Item -ItemType Directory -Force -Path "src/main/resources/fonts"

# Download Noto Color Emoji font
$url = "https://github.com/googlefonts/noto-emoji/raw/main/fonts/NotoColorEmoji.ttf"
$output = "src/main/resources/fonts/NotoColorEmoji.ttf"

Write-Host "Downloading Noto Color Emoji font..."
Invoke-WebRequest -Uri $url -OutFile $output

Write-Host "Font downloaded successfully to $output" 