/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat;

import java.util.List;
import org.pircbotx.Configuration;

public class XenoConf extends Configuration {

    public XenoConf(XenoBuilder b) {
        super(b);
    }

    public static class XenoBuilder extends Builder {

        public XenoBuilder() {
            super();
        }

        public XenoBuilder(Configuration configuration) {
            super(configuration);
        }

        public XenoBuilder(Builder otherBuilder) {
            super(otherBuilder);
        }

        public XenoBuilder addAutoJoinChannels(List<String> channels) {
            for (String channel : channels) {
                getAutoJoinChannels().put(channel, "");
            }
            return this;
        }
    }
}
