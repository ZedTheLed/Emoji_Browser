package com.kukumoraketo.emojibrowser.Emoji.Emoji;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by zed on 29.4.2017.
 */

public class EmojiLite extends EmojiMinimal implements Comparable<EmojiLite> {

    protected boolean hasTone;
    protected EmojiTone tone;
    protected int emojiOrder;
    protected EmojiCategory category;


    public EmojiLite(String unicode, boolean hasTone, EmojiTone tone, int emojiOrder, EmojiCategory category)
    {
        super(unicode);
        this.hasTone = hasTone;
        this.tone = tone;
        this.emojiOrder = emojiOrder;
        this.category = category;
    }

    public EmojiLite(String unicode, boolean hasTone, int tone, int emojiOrder, EmojiCategory category){
        this(unicode, hasTone, EmojiTone.getTone(tone), emojiOrder, category);
    }

    public EmojiLite(EmojiLite emojiLite){
        super(emojiLite.unicode);
        this.hasTone = emojiLite.hasTone();
        this.tone = emojiLite.getTone();
        this.emojiOrder = emojiLite.getEmojiOrder();
        this.category = emojiLite.getCategory();

    }


    @Override
    public int getDravableId(Context context) {
        // TODO
        return 0;
    }

    public boolean hasTone(){
        return this.hasTone;
    }

    public EmojiTone getTone(){
        return this.tone;
    }

    public int getEmojiOrder(){
        return this.emojiOrder;
    }

    public EmojiCategory getCategory(){
        return this.category;
    }

    @Override
    public int compareTo(@NonNull EmojiLite o) {

        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this.getEmojiOrder() < o.getEmojiOrder())
            return BEFORE;

        if (this.getEmojiOrder() > o.getEmojiOrder())
            return AFTER;

        return EQUAL;
    }
}
