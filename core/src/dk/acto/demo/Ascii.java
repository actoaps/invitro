package dk.acto.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

public class Ascii extends ApplicationAdapter {
	private SpriteBatch batch;
	private float width;
	private float height;
    private float angle1;
    private float angle2;
    private float angle3;
	private Board board;
	private Map<Tile, Texture> tileTextures;
	private Texture selector;
	private BitmapFont font;
	private TilePicker picker = new TilePicker();
    private Texture dot;
    private List<Match> matches = new LinkedList<>();
    private Texture moire;
    private Long score;
    private Long startTime;
    private long endTime;
    private GlyphLayout layout;

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

        this.layout = new GlyphLayout(); //dont do this every frame! Store it as member
         this.batch = new SpriteBatch();
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
        this.font = new BitmapFont( Gdx.files.internal("bitmap.fnt"),
                Gdx.files.internal("bitmap.png"), false);
		this.moire = new Texture("moire.png");
        this.selector = new Texture("selector.png");
        this.dot = new Texture("dot.png");
        this.score = 0L;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.enableBlending();
		batch.begin();


        this.angle1 +=.01;
        this.angle2 +=.02;
        this.angle3 +=.03;
        if (this.angle1 > PI *2) {
            this.angle1 -= PI*2;
        }

        if (this.angle2 > PI *2) {
            this.angle2 -= PI*2;
        }

        if (this.angle3 > PI *2) {
            this.angle3 -= PI*2;
        }



        if (board.isWon() && this.endTime == 0) {
            this.endTime = System.currentTimeMillis();
        }


        batch.setColor(Color.RED);
        batch.draw(moire, (float) (-32 + sin(this.angle1)*32), (float) (-32 + cos(this.angle1)*32));
        batch.setColor(Color.GREEN);
        batch.draw(moire, (float) (-32 + sin(this.angle2)*32), (float) (-32 + cos(this.angle2)*32));
        batch.setColor(Color.BLUE);
        batch.draw(moire, (float) (-32 + sin(this.angle3)*32), (float) (-32 + cos(this.angle3)*32));

        batch.setColor(Color.WHITE);

        for (Map.Entry<Integer, Tile> entry : board) {
			batch.draw(tileTextures.get(entry.getValue()),
                    Board.getX(entry.getKey()) * 32,
                    416 - Board.getY(entry.getKey()) * 32
			);
		}

		if (picker.getFirst() != null) {
		    batch.setColor(Color.GREEN);
            batch.draw(selector,
                    Board.getX(picker.getFirst()) * 32,
                    416 - Board.getY(picker.getFirst()) * 32
            );
		    if (picker.getLast() != null) {
                batch.setColor(Color.RED);
                batch.draw(selector,
                        Board.getX(picker.getLast()) * 32,
                        416 - Board.getY(picker.getLast()) * 32
                );
            }
        }

        if (picker.isReady()) {
            Match temp = board.matches(picker.getFirst(), picker.getLast());
            this.matches.add(temp);
            if (this.matches != null) {
                this.score = this.score + temp.getPath().size();
            }

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

        font.setColor(Color.WHITE);
        font.draw(batch, "Score " + score, 32,448);
        String time = "Time " + (((this.endTime == 0 ? System.currentTimeMillis() : this.endTime)-this.startTime)/1000);
        font.draw(batch, time , 32,32);
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
