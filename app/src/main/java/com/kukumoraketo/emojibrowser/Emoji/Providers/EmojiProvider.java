package com.kukumoraketo.emojibrowser.Emoji.Providers;

import com.kukumoraketo.emojibrowser.Emoji.Emoji.EmojiCategory;
import com.kukumoraketo.emojibrowser.Emoji.Emoji.EmojiLite;
import com.kukumoraketo.emojibrowser.Emoji.Emoji.EmojiTone;

import java.util.List;

/**
 * Created by zed on 1.5.2017.
 */

public interface EmojiProvider {

    /**
     * Returns emojis of specified category and tone.
     * It also returns emojis withougt tone
     * @param category category of emojis
     * @return emojis with desired category and (desired tone or with no-tone)
     */
    List<EmojiLite> getEmoji (EmojiCategory category);

}