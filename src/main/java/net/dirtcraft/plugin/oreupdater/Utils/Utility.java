package net.dirtcraft.plugin.oreupdater.Utils;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class Utility {

    public static Text format(String message) {
        return TextSerializers.FORMATTING_CODE.deserialize(message);
    }

}
