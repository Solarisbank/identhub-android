phrase -t $PHRASE_ACCESS_TOKEN pull

directories=(
    "./qes/src/main/res/values-en"
    "./fourthline/src/main/res/values-en"
    "./bank/src/main/res/values-en"
)

for dir in "${directories[@]}"; do
    destination="${dir/values-en/values}"
    rsync -a --ignore-existing "$dir/" "$destination/"
    rm -rf "$dir"
done

echo "Content merging and source directory deletion complete."