package dk.acto.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.sun.glass.ui.Pixels;

import java.util.*;

import static com.badlogic.gdx.graphics.GL20.*;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;

public class Ascii extends ApplicationAdapter {
	private SpriteBatch batch;
	private float width;
	private float height;
	private Board board;
	private Map<Tile, Texture> tileTextures;
	private Texture selector;
	private TilePicker picker = new TilePicker();
    private Texture dot;
    private List<Match> matches = new LinkedList<>();

    @Override
	public void create () {
        Gdx.graphics.setWindowedMode(448, 448);
        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                Integer position = Board.getPosition(screenX / 32, screenY / 32);
                if (board.getTile(position) != null) {
                    return picker.pick(position, board.getTile(position));
                }
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(int amount) {
                return false;
            }
        });

        batch = new SpriteBatch();
		this.width = Gdx.graphics.getWidth();
		this.height = Gdx.graphics.getHeight();
		this.board = new Board();
		this.tileTextures = new HashMap<>();
		this.tileTextures.put(Tile.ALPHA, new Texture("alpha.png"));
		this.tileTextures.put(Tile.BRAVO, new Texture("bravo.png"));
		this.tileTextures.put(Tile.CHARLIE, new Texture("charlie.png"));
		this.tileTextures.put(Tile.DELTA, new Texture("delta.png"));
		this.tileTextures.put(Tile.ECHO, new Texture("echo.png"));
		this.tileTextures.put(Tile.FOXTROT, new Texture("foxtrot.png"));
		this.tileTextures.put(Tile.GOLF, new Texture("golf.png"));
		this.tileTextures.put(Tile.HOTEL, new Texture("hotel.png"));
        this.selector = new Texture("selector.png");
        this.dot = new Texture("dot.png");
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();
		batch.setColor(Color.WHITE);

		for (Map.Entry<Integer, Tile> entry : board) {
			batch.draw(tileTextures.get(entry.getValue()),
                    Board.getX(entry.getKey()) * 32,
                    418 - Board.getY(entry.getKey()) * 32
			);
		}

		if (picker.getFirst() != null) {
		    batch.setColor(Color.GREEN);
            batch.draw(selector,
                    Board.getX(picker.getFirst()) * 32,
                    418 - Board.getY(picker.getFirst()) * 32
            );
		    if (picker.getLast() != null) {
                batch.setColor(Color.RED);
                batch.draw(selector,
                        Board.getX(picker.getLast()) * 32,
                        418 - Board.getY(picker.getLast()) * 32
                );
            }
        }

        if (picker.isReady()) {
            this.matches.add(board.matches(picker.getFirst(), picker.getLast()));
		    picker.reset();
        }

        if (this.matches != null && !this.matches.isEmpty()) {
            batch.setColor(Color.TEAL);
            for (Match match : matches) {
                this.board.handleMatch(match);
                if (match.getRenderQueue().isEmpty()) {
                    this.matches.remove(match);
                }

                for (Integer integer : match.getRenderQueue()) {
                    batch.draw(dot,
                            Board.getX(integer) * 32,
                            418 - Board.getY(integer) * 32
                    );
                }
            }
        }

		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		for (Texture texture : tileTextures.values()) {
			texture.dispose();
		}
	}
}
