import * as services from "../service/GCE3";
import { gce3, setAdfsUser, GCE3 } from "../service/GCE3";

// Set document.cookie at the top of the file
document.cookie = "sid=1789432; ctsi_CountryName=USA; ctsi_Lob=LOB; ctsi_Taxonomy=Taxonomy";

const gce3Local = {
  user: {
    city: "Chicago",
    country: "USA",
    countryName: "United States",
    fg: "",
    firstname: "Matthew",
    fullName: "Matthew Anderson",
    heritage: "",
    lastname: "Anderson",
    lob: "",
    middleinitial: "",
    region: "NAMR",
    sid: "1789432",
    state: "IL",
    stateName: "Illinois",
    subLob: "",
    taxonomy: ""
  }
};

const emptyUserWith = (specific) => (target) => {
  return Object.assign(target, {
    city: "",
    country: "",
    countryName: "",
    fg: "",
    firstname: "",
    fullName: "",
    heritage: "",
    lastname: "",
    lob: "",
    middleinitial: "",
    region: "",
    sid: "",
    state: "",
    stateName: "",
    subLob: "",
    taxonomy: "",
    ...specific
  });
};

describe("GCE3", () => {
  beforeEach(() => {
    window.jpmc.epmoss.user = "undefined";
    jest.clearAllMocks();
  });

  it("creates a gce3 object with the correct user information for dev", () => {
    const gce3Instance = GCE3("development");

    expect(gce3Instance).toBeDefined();
    expect(gce3Instance.user).toEqual({
      city: "",
      country: "USA",
      countryName: "",
      fg: "",
      firstname: "Guest",
      fullName: "Guest",
      heritage: "",
      lastname: "",
      lob: "LOB",
      middleinitial: "",
      region: "",
      sid: "1789432",
      state: "",
      stateName: "",
      subLob: "",
      taxonomy: "Taxonomy"
    });
  });

  it("creates a gce3 object with the correct user information for prod", () => {
    const gce3Instance = GCE3("production");

    expect(gce3Instance).toBeDefined();
    expect(gce3Instance.user).toEqual({
      city: "",
      country: "USA",
      countryName: "",
      fg: "",
      firstname: "Guest",
      fullName: "Guest",
      heritage: "",
      lastname: "",
      lob: "LOB",
      middleinitial: "",
      region: "",
      sid: "1789432",
      state: "",
      stateName: "",
      subLob: "",
      taxonomy: "Taxonomy"
    });
  });

  it("creates a gce3 object with the correct user information for non", () => {
    const gce3Instance = GCE3("");

    expect(gce3Instance).toBeDefined();
    expect(gce3Instance.user).toEqual({
      city: "",
      country: "USA",
      countryName: "",
      fg: "",
      firstname: "Guest",
      fullName: "Guest",
      heritage: "",
      lastname: "",
      lob: "LOB",
      middleinitial: "",
      region: "",
      sid: "1789432",
      state: "",
      stateName: "",
      subLob: "",
      taxonomy: "Taxonomy"
    });
  });

  it("ignores setAdfsUser when no user was provided", () => {
    setAdfsUser({});
    const gce3Instance = GCE3("");

    expect(gce3Instance.user).toEqual({
      city: "",
      country: "USA",
      countryName: "",
      fg: "",
      firstname: "Guest",
      fullName: "Guest",
      heritage: "",
      lastname: "",
      lob: "LOB",
      middleinitial: "",
      region: "",
      sid: "1789432",
      state: "",
      stateName: "",
      subLob: "",
      taxonomy: "Taxonomy"
    });
  });

  it("sets user information correctly with setAdfsUser", () => {
    const adfsUser = {
      sid: "123456",
      firstname: "John",
      lastname: "Doe"
    };

    setAdfsUser(adfsUser);
    const gce3Instance = GCE3("");

    expect(gce3Instance.user).toEqual({
      city: "",
      country: "",
      countryName: "",
      fg: "",
      firstname: "John",
      fullName: "",
      heritage: "",
      lastname: "Doe",
      lob: "",
      middleinitial: "",
      region: "",
      sid: "123456",
      state: "",
      stateName: "",
      subLob: "",
      taxonomy: ""
    });
  });
});
