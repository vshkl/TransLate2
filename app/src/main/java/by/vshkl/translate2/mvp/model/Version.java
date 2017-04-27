package by.vshkl.translate2.mvp.model;

public class Version {

    private int versionCode;
    private String versionName;
    private String status;
    private String size;
    private String link;
    private String filename;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Version)) {
            return false;
        }

        Version version = (Version) o;

        return getVersionCode() == version.getVersionCode() && (getVersionName() != null
                ? getVersionName().equals(version.getVersionName())
                : version.getVersionName() == null);
    }

    @Override
    public int hashCode() {
        int result = getVersionCode();
        result = 31 * result + (getVersionName() != null ? getVersionName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Version{");
        sb.append("versionCode=").append(versionCode);
        sb.append(", versionName='").append(versionName).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", size='").append(size).append('\'');
        sb.append(", link='").append(link).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
