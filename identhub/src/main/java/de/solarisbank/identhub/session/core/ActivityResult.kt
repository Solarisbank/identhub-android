package de.solarisbank.identhub.session.core

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
class ActivityResult(val resultCode: Int, val data: Intent?) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readParcelable(Intent::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(resultCode)
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActivityResult> {
        override fun createFromParcel(parcel: Parcel): ActivityResult {
            return ActivityResult(parcel)
        }

        override fun newArray(size: Int): Array<ActivityResult?> {
            return arrayOfNulls(size)
        }
    }

}
