package net.freenode.xenomorph.xenomat.jettyServlets;

import javax.servlet.http.HttpServletRequest;

public class SessionHelper {

    private static String authenticated = "authenticated";

    public static boolean isAdmin(HttpServletRequest hsr) {
        if (hsr.getSession().getAttribute(authenticated) == true) {
            return true;
        }
        return false;
    }

    public static void setAdmin(HttpServletRequest hsr) {
        hsr.getSession().setAttribute(authenticated, true);
    }

    public static void removeAdmin(HttpServletRequest hsr) {
        hsr.getSession().setAttribute(authenticated, false);
    }

    static void destroySession(HttpServletRequest hsr) {
        hsr.getSession().invalidate();
    }
}
