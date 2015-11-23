#! /bin/bash

url="https://content.dropboxapi.com/1/files_put/auto/$DROPBOX_APK_PATH?access_token=$DROPBOX_ACCESS_TOKEN"
response="$(curl --silent --insecure --upload-file "$DEBUG_APK_PATH" "$url")"
ec="$?"
if [ "$ec" -ne 0 ]
then
    printf "Curl error\n"
    exit "$ec"
fi
if grep -q 'error' <<< "$response"
then
    err_msg="$(sed 's/{"error": "\(.*\)"}/\1/' <<< "$response")"
    printf "$err_msg\n"
    exit 1
fi
printf "Upload is successfully finished\n"
