package thiovan.submission3.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TvShowList implements Parcelable {
    private String id;
    private String photo;
    private String name;
    private String description;

    public TvShowList() {
    }

    private TvShowList(Parcel in) {
        id = in.readString();
        photo = in.readString();
        name = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(photo);
        dest.writeString(name);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TvShowList> CREATOR = new Creator<TvShowList>() {
        @Override
        public TvShowList createFromParcel(Parcel in) {
            return new TvShowList(in);
        }

        @Override
        public TvShowList[] newArray(int size) {
            return new TvShowList[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
