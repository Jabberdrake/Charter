package dev.jabberdrake.charter.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class TextUtils {
    public static final TextColor DARK_ZORBA = TextColor.color(0x4b4047);
    public static final TextColor ZORBA = TextColor.color(0x9f918d);
    public static final TextColor LIGHT_ZORBA = TextColor.color(0xdadada);

    public static final TextColor DARK_LAUREL = TextColor.color(0x00a800);
    public static final TextColor LAUREL = TextColor.color(0x97ca97);
    public static final TextColor LIGHT_LAUREL = TextColor.color(0x54fc54);

    public static final TextColor DARK_BRASS = TextColor.color(0xb49f3e);
    public static final TextColor BRASS = TextColor.color(0xd9ca81);
    public static final TextColor LIGHT_BRASS = TextColor.color(0xfce143);

    public static final TextColor DARK_AMETHYST = TextColor.color(0x9a58fc);
    public static final TextColor AMETHYST = TextColor.color(0xaa76dd);
    public static final TextColor LIGHT_AMETHYST = TextColor.color(0xc49ffc);

    public static final TextColor DARK_CHROME = TextColor.color(0x8b9db3);
    public static final TextColor LIGHT_CHROME = TextColor.color(0xd1e7e5);

    public static final TextColor DARK_MANA = TextColor.color(0x5d9da8);
    public static final TextColor LIGHT_MANA = TextColor.color(0x84e1e1);

    public static final TextColor LIGHT_ROSEMETAL = TextColor.color(0xf5afaf);
    public static final TextColor ROSEMETAL = TextColor.color(0xfc7c96);

    public static final TextColor LIVINGMETAL = TextColor.color(0x5afc9f);

    public static Component composeInfoPrefix() {
        return Component.text("[!] > ", LIGHT_ZORBA);
    }

    public static Component composeInfoText(String content) {
        return Component.text(content, ZORBA);
    }

    public static Component composeInfoHighlight(String content) {
        return Component.text(content, LIGHT_ZORBA);
    }

    public static Component composePlainInfoMessage(String content) {
        return Component.text()
                .append(composeInfoPrefix())
                .append(composeInfoText(content))
                .build();
    }

    public static Component composeErrorPrefix() {
        return Component.text("[⚠] > ", NamedTextColor.RED);
    }

    public static Component composeErrorText(String content) {
        return Component.text(content, NamedTextColor.DARK_RED);
    }

    public static Component composeErrorHighlight(String content) { return Component.text(content, NamedTextColor.RED); }

    public static Component composePlainErrorMessage(String content) {
        return Component.text()
                .append(composeErrorPrefix())
                .append(composeErrorText(content))
                .build();
    }

    public static Component composeSuccessPrefix() {
        return Component.text("[❊] > ", LIGHT_LAUREL);
    }

    public static Component composeSuccessText(String content) {
        return Component.text(content, LAUREL);
    }

    public static Component composeSuccessHighlight(String content) {
        return Component.text(content, DARK_LAUREL);
    }

    public static Component composePlainSuccessMessage(String content) {
        return Component.text()
                .append(composeSuccessPrefix())
                .append(composeSuccessText(content))
                .build();
    }

    public static Component composeOperatorPrefix() {
        return Component.text("[※] > ", LIGHT_CHROME);
    }

    public static Component composeOperatorText(String content) {
        return Component.text(content, DARK_CHROME);
    }

    public static Component composeOperatorHighlight(String content) {
        return Component.text(content, LIGHT_CHROME);
    }

    public static Component composePlainOperatorMessage(String content) {
        return Component.text()
                .append(composeOperatorPrefix())
                .append(composeOperatorText(content))
                .build();
    }

}
