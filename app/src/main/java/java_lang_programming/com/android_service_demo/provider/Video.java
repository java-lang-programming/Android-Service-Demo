/**
 * Copyright (C) 2016 Programming Java Android Development Project
 * Programming Java is
 * <p/>
 * http://java-lang-programming.com/
 * <p/>
 * ContentProvider Generator version : 0.1.0
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package java_lang_programming.com.android_service_demo.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;

public class Video {

    public static final String TAG = "Video";
    // id
    public long id;
    // artist
    public String artist;
    // title
    public String title;
    // album
    public String album;
    // duration
    public long duration;
    // latitude
    public String latitude;
    // longitude
    public String longitude;
    // path
    public String path;

    /**
     * create Thumbnail
     *
     * @param context
     * @return MediaStore.Video.Thumbnails(MediaStore.Images.Thumbnails.MINI_KIND)
     */
    public Bitmap getThumbnail(final Context context) {
        if (id == 0) {
            return null;
        }
        ContentResolver cr = context.getContentResolver();
        Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
        return thumbnail;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("Video [");
        str.append(" id=" + id);
        if (!TextUtils.isEmpty(artist)) {
            str.append(", artist=" + artist);
        }
        if (!TextUtils.isEmpty(title)) {
            str.append(", title=" + title);
        }
        if (!TextUtils.isEmpty(album)) {
            str.append(", album=" + album);
        }
        str.append(", duration=" + duration);
        if (!TextUtils.isEmpty(latitude)) {
            str.append(", latitude=" + latitude);
        }
        if (!TextUtils.isEmpty(longitude)) {
            str.append(", longitude=" + longitude);
        }
        if (!TextUtils.isEmpty(path)) {
            str.append(", path=" + path);
        }
        str.append("]");
        return str.toString();
    }

}