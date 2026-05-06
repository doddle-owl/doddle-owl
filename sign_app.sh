#!/bin/zsh

VERSION=${1:-"26.5.1"}
APP_NAME="DODDLE-OWL.app"
DMG_NAME="doddle-owl-${VERSION}.dmg"

# 1. 第一引数からentitlements.txtのパスを取得（指定がなければデフォルト値を使用）
ENTITLEMENTS=${1:-"/Users/morita/doddle-owl/entitlements.txt"}

# 2. 署名に使用するID
SIGNER="Developer ID Application: Takeshi Morita"

# 署名対象のリスト（配列）
TARGETS=(
    "$APP_NAME/Contents/runtime/Contents/Home/bin/jrunscript"
    **/*.jar
    **/**/java
    **/**/keytool
    **/**/jspawnhelper
    **/*.dylib
    "$APP_NAME/Contents/MacOS/DODDLE-OWL"
)

echo "Using entitlements: $ENTITLEMENTS"

# 既存の署名を削除
codesign --remove-signature "$APP_NAME/"

JAR_PATH="$APP_NAME/Contents/app/doddle-owl-$VERSION-all.jar"
DYLIBS=(
    "com/formdev/flatlaf/natives/libflatlaf-macos-x86_64.dylib"
    "com/formdev/flatlaf/natives/libflatlaf-macos-arm64.dylib"
    "org/sqlite/native/Mac/aarch64/libsqlitejdbc.dylib"
    "org/sqlite/native/Mac/x86_64/libsqlitejdbc.dylib"
)

echo "Processing JAR: $JAR_PATH"

TEMP_DIR=$(mktemp -d)

unzip -q "$JAR_PATH" -d "$TEMP_DIR"

TARGET_DYLIB="$TEMP_DIR/$DYLIB_INTERNAL_PATH"

for dylib_path in "${DYLIBS[@]}"; do
    TARGET_DYLIB="$TEMP_DIR/$dylib_path"
    
    if [ -f "$TARGET_DYLIB" ]; then
        echo "Signing: $dylib_path"
        codesign --entitlements "$ENTITLEMENTS" -f --options=runtime --timestamp -s "$SIGNER" "$TARGET_DYLIB"
    else
        echo "Warning: dylib not found, skipping: $dylib_path"
    fi
done

echo "Re-packaging JAR..."
(cd "$TEMP_DIR" && zip -r -q "$(basename "$JAR_PATH")" .)
mv "$TEMP_DIR/$(basename "$JAR_PATH")" "$JAR_PATH"

rm -rf "$TEMP_DIR"

echo "Internal signing complete!"

# 各ターゲットに対して署名を実行
for target in "${TARGETS[@]}"; do
    # ファイルが存在する場合のみ実行（ワイルドカード展開用）
    codesign --entitlements "$ENTITLEMENTS" -f --options=runtime --timestamp -s "$SIGNER" $target
done

echo "Target: $APP_NAME"
echo "Version: $VERSION"

if [ -f "$DMG_NAME" ]; then
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    BACKUP_NAME="doddle-owl-${VERSION}_${TIMESTAMP}.dmg"
    
    echo "Existing file found. Backing up to: $BACKUP_NAME"
    mv "$DMG_NAME" "$BACKUP_NAME"
fi

echo "Creating new DMG: $DMG_NAME..."

hdiutil create -format UDBZ -plist -srcfolder "$APP_NAME" "$DMG_NAME"

if [ $? -eq 0 ]; then
    echo "--------------------------------------"
    echo "Success: $DMG_NAME has been created."
    echo "--------------------------------------"
else
    echo "Error: DMG creation failed."
    exit 1
fi

