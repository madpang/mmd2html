package dev.madpang.ast.blocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
/**
 * Represents a raw text block (for lines not yet parsed into richer block types).
 */
public class RawTextBlock {
    public List<String> lines = new ArrayList<>();
    /// @todo
    public static RawTextBlock parse(BufferedReader reader, String firstLine) throws IOException {
        RawTextBlock block = new RawTextBlock();
        return block;
    }
}
