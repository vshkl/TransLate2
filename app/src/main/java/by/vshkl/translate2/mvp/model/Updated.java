package by.vshkl.translate2.mvp.model;

public class Updated {

    private long updatedTimestamp;

    public long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Updated updated = (Updated) o;

        return updatedTimestamp == updated.updatedTimestamp;
    }

    @Override
    public int hashCode() {
        return (int) (updatedTimestamp ^ (updatedTimestamp >>> 32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Updated{");
        sb.append("updatedTimestamp=").append(updatedTimestamp);
        sb.append('}');
        return sb.toString();
    }
}
