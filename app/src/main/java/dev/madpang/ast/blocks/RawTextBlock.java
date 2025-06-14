package dev.madpang.ast.blocks;

/**
 * Represents a raw text block (for lines not yet parsed into richer block types).
 */
public class RawTextBlock implements IBlock {
    public final String text;
    public RawTextBlock(String text) {
        this.text = text;
    }
}
