package dk.acto.demo;

import java.util.LinkedList;
import java.util.Queue;

public class Match {
    private final Queue<Integer> path;
    private final Queue<Integer> renderQueue;
    private final Integer startBlock;
    private final Integer stopBlock;


    public Match(Queue<Integer> path, Integer startBlock, Integer stopBlock) {
        this.path = path;
        this.renderQueue = new LinkedList<>();
        this.startBlock = startBlock;
        this.stopBlock = stopBlock;
    }

    public static Match empty() {
        return new Match(
                new LinkedList<Integer>(),
                null,
                null);
    }

    public Queue<Integer> getPath() {
        return path;
    }

    public Queue<Integer> getRenderQueue() {
        return renderQueue;
    }

    public Integer getStartBlock() {
        return startBlock;
    }

    public Integer getStopBlock() {
        return stopBlock;
    }
}
