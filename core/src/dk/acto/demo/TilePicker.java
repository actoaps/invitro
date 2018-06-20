package dk.acto.demo;

public class TilePicker {
    private Integer first;
    private Tile firstTile;
    private Integer last;

    public boolean pick (Integer position, Tile tile) {

        if (this.first == null) {
            this.first = position;
            this.firstTile = tile;
            return true;
        }

        if (this.last == null) {
            if(this.firstTile != tile) {
                this.first = position;
                this.firstTile = tile;
                return true;
            }

            if (this.first.equals(position)) {
                this.first = null;
                return true;
            }
            this.last = position;
            return true;
        }
        return true;
    }

    public Integer getFirst() {
        return first;
    }

    public Integer getLast() {
        return last;
    }

    public boolean isReady() {
        return this.first != null && this.last != null;
    }

    public void reset() {
        this.first = null;
        this.last = null;
        this.firstTile = null;
    }
}
