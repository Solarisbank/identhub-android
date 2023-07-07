phrase -t $PHRASE_ACCESS_TOKEN pull

directories=(
  "qes/src/main/res/values-en:qes/src/main/res/values"
  "fourthline/src/main/res/values-en:fourthline/src/main/res/values"
  "bank/src/main/res/values-en:bank/src/main/res/values"
  "startup/src/main/res/values-en:startup/src/main/res/values"
)

file_to_replace="strings.xml"

for dir in "${directories[@]}"; do
  source="${dir%:*}/${file_to_replace}"
  destination="${dir#*:}/"
  rsync -a --remove-source-files "$source" "$destination"
  rm -rf "${dir%:*}"
done

echo "Content merging and source directory deletion complete."
