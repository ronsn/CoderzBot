/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.freenode.xenomorph.xenomat.jettyHandlers;

public class HtmlHelper {

    public static String getHeader(String title) {
        return "<html>\r\n  <head>\r\n    <title>" + title + "</title>\r\n  </head>\r\n  <body>\r\n";
    }

    public static String getFooter() {
        return "  </body>\r\n</html>";
    }
}
