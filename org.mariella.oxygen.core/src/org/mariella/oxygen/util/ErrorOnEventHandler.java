/*
* http://oxygen.io, The Oxygen.io Persistence Framework
* Copyright 2001-2006 by Troyer Information Systems GmbH / Austria
* http://troyer.co.at http://oxygen.io http://oxygen.pmi.io
*
* Licensed under the terms of the LGPL (GNU Lesser General Public License)
* version 2.1. See the license text under: http://www.fsf.org or
* http://www.opensource.org/licenses/lgpl-license.php
*
* All source files must contain this header info!
* Authorship can only be claimed for new classes which are not part
* of 1.0.000 version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*/

package org.mariella.oxygen.util;


/**
* @since: 1.0.000
* @version: 1.0.000
*/
public class ErrorOnEventHandler {
    /**
     * ErrorOnEventHandler constructor comment.
     */    public ErrorOnEventHandler() {
        super();
    }
    public static void handleException(Exception e) {
        e.printStackTrace();
    }
}
