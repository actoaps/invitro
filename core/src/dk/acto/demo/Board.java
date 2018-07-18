package dk.acto.demo;

import java.util.*;

public class Board implements Iterable<Map.Entry<Integer, Tile>> {
    private final Map<Integer, Tile> board;
    private static final Integer size = 12;

    public Board() {
        this.board = new HashMap<>();
        Queue<Tile> tiles = genTiles();
        for (int y = 1; y < size + 1; y++) {
            for (int x = 1; x < size + 1; x++) {
                this.board.put(getPosition(x, y), tiles.remove());
            }
        }
    }

    public static Integer getPosition(Integer x, Integer y) {
        return x + y * (size + 2);
    }

    public static Integer getX(Integer i) {
        return i % (size + 2);
    }

    public static Integer getY(Integer i) {
        return i / (size + 2);
    }

    @Override
    public Iterator<Map.Entry<Integer, Tile>> iterator() {
        return board.entrySet().iterator();
    }

    private Queue<Tile> genTiles() {
        LinkedList<Tile> result = new LinkedList<>();
        for (Tile tile : Tile.values()) {
            for (int i = 0; i < (size * size / 8); i++) {
                result.add(tile);
            }
        }
        Collections.shuffle(result);
        return result;
    }

    public Tile getTile(Integer position) {
        if (board.containsKey(position)) {
            return board.get(position);
        } else {
            return null;
        }
    }

    public Match matches(Integer first, Integer last) {
        Integer manDist = Math.abs(getX(last) - getX(first)) + Math.abs(getY(last) - getY(first));
        if (manDist < 2) {
            return Match.empty();
        }

        LinkedList<Integer> tPath = new LinkedList<>();

        Queue<PathSegment> path = getPath(getX(first), getY(first), getX(last), getY(last));
        for (PathSegment pathSegment : path) {
            tPath.add(getPosition(pathSegment.x, pathSegment.y));
        }

        if (tPath.size() > 0) {
                return new Match(
                        tPath,
                        first,
                        last);
        }

        return Match.empty();
    }

    private Queue<PathSegment> getPath(Integer x, Integer y, Integer xx, Integer yy) {
        Queue<LinkedList<PathSegment>> workQueue = new PriorityQueue<LinkedList<PathSegment>>(new Comparator<LinkedList<PathSegment>>() {
            @Override
            public int compare(LinkedList<PathSegment> o1, LinkedList<PathSegment> o2) {
                return Integer.compare(o1.size(), o2.size());
            }
        });

        LinkedList<PathSegment> iPath = new LinkedList<>();
        iPath.add(new PathSegment(x, y, "N", "N"));
        workQueue.add(iPath);

        iPath = new LinkedList<>();
        iPath.add(new PathSegment(x, y, "S", "S"));
        workQueue.add(iPath);

        iPath = new LinkedList<>();
        iPath.add(new PathSegment(x, y, "E", "E"));
        workQueue.add(iPath);

        iPath = new LinkedList<>();
        iPath.add(new PathSegment(x, y, "W", "W"));
        workQueue.add(iPath);

        while (!workQueue.isEmpty()) {
            LinkedList<PathSegment> cPath = workQueue.remove();
            Queue<PathSegment> done = addNeighbours(workQueue, cPath, getPosition(xx, yy));
            if (done != null) {
                return done;
            }
        }
        return new LinkedList<>();
    }

    private Queue<PathSegment> addNeighbours(Collection<LinkedList<PathSegment>> workStack, LinkedList<PathSegment> cPath, Integer position) {
        PathSegment cTile = cPath.getLast();

        // North
        PathSegment nTile = new PathSegment(cTile.x, cTile.y - 1, cTile.directionSwitch, "N");
        Queue<PathSegment> done = checkTile(workStack, cPath, position, nTile);
        if (done != null) {
            return done;
        }

        // South
        nTile = new PathSegment(cTile.x, cTile.y + 1, cTile.directionSwitch, "S");
        done = checkTile(workStack, cPath, position, nTile);
        if (done != null) {
            return done;
        }

        // East
        nTile = new PathSegment(cTile.x + 1, cTile.y, cTile.directionSwitch, "E");
        done = checkTile(workStack, cPath, position, nTile);
        if (done != null) {
            return done;
        }

        // West
        nTile = new PathSegment(cTile.x - 1, cTile.y, cTile.directionSwitch, "W");
        done = checkTile(workStack, cPath, position, nTile);
        if (done != null) {
            return done;
        }
        return null;
    }

    private LinkedList<PathSegment> checkTile(Collection<LinkedList<PathSegment>> workStack, LinkedList<PathSegment> cPath, Integer position, PathSegment nTile) {

        if (nTile.directionSwitch.length() > 3) {
            return null;
        }

        if (position.equals(getPosition(nTile.x, nTile.y))) {
            cPath.add(nTile);
            return cPath;
        }

        if (getTile(getPosition(nTile.x, nTile.y)) != null) {
            return null;
        }

        if (cPath.size() > 35) {
            return null;
        }

        if (nTile.x < 0) {
            return null;
        }

        if (nTile.x > size + 1) {
            return null;
        }

        if (nTile.y < 0) {
            return null;
        }

        if (nTile.y > size + 1) {
            return null;
        }

        LinkedList<PathSegment> nPath = new LinkedList<PathSegment>(cPath);
        nPath.add(nTile);
        workStack.add(nPath);
        return null;
    }

    public void handleMatch(Match match) {
        if (!match.getPath().isEmpty()) {
            Integer cPos = match.getPath().remove();
            match.getRenderQueue().add(cPos);
            return;
        }

        if (this.getTile(match.getStartBlock()) != null) {
            this.board.remove(match.getStartBlock());
            this.board.remove(match.getStopBlock());
        }

        if (!match.getRenderQueue().isEmpty()) {
            match.getRenderQueue().remove();
        }
    }

    public boolean isWon() {
        for (int y = 1; y < size + 1; y++) {
            for (int x = 1; x < size + 1; x++) {
                if (getTile(getPosition(x,y)) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private class PathSegment {
        private final Integer x, y;
        private String directionSwitch;

        private PathSegment(Integer x, Integer y, String oldDirection, String newDirection) {
            this.x = x;
            this.y = y;
            if ("".equals(oldDirection)) {
                this.directionSwitch = newDirection;
            } else {
                if (newDirection.equals(oldDirection.substring(oldDirection.length() - 1))) {
                    this.directionSwitch = oldDirection;
                } else {
                    this.directionSwitch = oldDirection + newDirection;
                }
            }
        }

        @Override
        public String toString() {
            return "PathSegment{" +
                    "x=" + x +
                    ", y=" + y +
                    ", directionSwitch='" + directionSwitch + '\'' +
                    '}';
        }
    }
}
