let mode;

// Main GCE3 object
const gce3 = {
    user: {},
    tabs: [],
    apptraytabs: [],
    errors: "GCE3 Debug",
    apptrayLOBdata: {},
    protocol: (document.location.protocol.toLowerCase().indexOf("https") >= 0) ? 'https://' : 'http://',
    mode: mode || "dev",
    recentSearches: [],
    BlockedExecutiveSIDs: [],
    ForeseeIncludeURLs: [],
    foreseeFileLoadCheck: 0,

    // Method to populate user data if not already populated
    populateUser: function() {
        if (!this.user.sid) this.parseCookie();
    },

    // Method to parse cookies and populate user data
    parseCookie: function() {
        this.user = {
            country: '',
            countryName: '',
            sid: '',
            region: '',
            lob: '',
            city: '',
            sublob: '',
            fg: '',
            heritage: '',
            state: '',
            stateName: '',
            fullname: '',
            firstname: '',
            lastname: ''
        };

        this.sessCookie = this.getSessCookie("s_pens");
        let esourceStrCookie = '';

        const strCookie = this.getCookie("strCookieField");
        esourceStrCookie = this.getCookie("eSourceGate140");

        if (strCookie && strCookie !== "_notSet") {
            const arrCookie = strCookie.split("|");
            const strFullName = arrCookie[1].trim().replace(/\s+/g, " ");
            populatePersonalNames(strFullName, this);

            this.user.sid = arrCookie[2].trim();
            this.user.region = arrCookie[3].trim();
            this.user.country = this.getCookie("country");
            this.user.lob = this.getCookie("cts_Lob");
            this.user.taxonomy = this.getCookie("cts1_taxonomy");

            parseGceCountry(arrCookie[5], this);
            parseGceState(arrCookie[6], this);
            this.user.city = arrCookie[7].trim();
            this.user.sublob = arrCookie[5].trim();
            this.user.fg = "";
            this.user.heritage = "";
        } else if (esourceStrCookie) {
            const arrCookie = esourceStrCookie.split("|");
            const strFullName = arrCookie[1].trim().replace(/\s+/g, " ");
            populatePersonalNames(strFullName, this);

            this.user.sid = arrCookie[2].trim();
            this.user.region = arrCookie[3].trim();
            this.user.lob = arrCookie[5].trim();
            this.user.city = arrCookie[7].trim();
            this.user.heritage = arrCookie[7].trim();
        }

        if (!this.user.sid) {
            this.user.fullname = "Guest";
            this.user.firstname = "Guest";
            this.user.lastname = "";
        }
    },

    // Helper methods
    getCookie: function(strCookieField) {
        // Implementation here
    },

    getSessCookie: function() {
        let sGuid = "";
        const len = 10;
        const digits = new Uint8Array(len);

        for (let i = 0; i < len; i++) {
            sGuid += digits[i].toString(16);
        }

        return sGuid.substring(0, len);
    }
};

// Exported function to set ADFS user
export const setAdfsUser = (adfsUser) => {
    if (adfsUser && adfsUser.sid) {
        gce3.user = adfsUser;
    }
};

// Factory function to initialize GCE3 with the given environment
export const GCE3 = (options) => {
    switch (options) {
        case 'development':
            mode = 'dev';
            break;
        case 'uat':
            mode = 'uat';
            break;
        case 'production':
            mode = 'prod';
            break;
        default:
            mode = 'dev';
            break;
    }

    // Initialize user object
    gce3.user = {
        fullname: '',
        firstname: '',
        middleinitial: '',
        lastname: '',
        sid: '',
        region: '',
        country: '',
        state: '',
        city: '',
        sublob: '',
        heritage: '',
        fg: '',
        stateName: '',
        countryName: '',
        taxonomy: ''
    };

    // Ensure necessary methods are defined
    gce3.populateUser();
    return gce3;
};

export default gce3;
