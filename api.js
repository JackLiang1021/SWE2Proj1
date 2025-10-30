import {
  Course,
  Program,
  ProgramVersion,
  RequirementGroup,
  Student
} from "./models.js";

async function getJSON(url) {
  const res = await fetch(url, { credentials: "omit" });
  if (!res.ok) throw new Error(`${res.status} ${res.statusText} for ${url}`);
  return res.json();
}

export class DegreeApi {
  constructor(baseUrl) {
    this.baseUrl = baseUrl;
  }

  async getStudent(id) {
    const dto = await getJSON(`${this.baseUrl}/students/${encodeURIComponent(id)}`);
    return Student.fromDTO(dto);
  }

  async getProgram(code) {
    const dto = await getJSON(`${this.baseUrl}/programs/${encodeURIComponent(code)}`);
    return Program.fromDTO(dto);
  }

  async getProgramVersion(code, year) {
    const dto = await getJSON(
      `${this.baseUrl}/programs/${encodeURIComponent(code)}/versions/${encodeURIComponent(year)}`
    );
    return ProgramVersion.fromDTO(dto);
  }

  async getRequirementGroups(code, year) {
    const arr = await getJSON(
      `${this.baseUrl}/programs/${encodeURIComponent(code)}/versions/${encodeURIComponent(year)}/groups`
    );
    return arr.map(RequirementGroup.fromDTO);
  }

  async getCourse(code) {
    const dto = await getJSON(`${this.baseUrl}/courses/${encodeURIComponent(code)}`);
    return Course.fromDTO(dto);
  }

  async getAllCourses() {
    const arr = await getJSON(`${this.baseUrl}/courses`);
    return arr.map(Course.fromDTO);
  }

  async getPrerequisites(code) {
    const arr = await getJSON(
      `${this.baseUrl}/courses/${encodeURIComponent(code)}/prerequisites`
    );
    return arr.map(Course.fromDTO);
  }

  async loadVersionSnapshot(programCode, catalogYear) {
    const program = await this.getProgram(programCode);
    const version = await this.getProgramVersion(programCode, catalogYear);
    const groups  = await this.getRequirementGroups(programCode, catalogYear);

    version.groups = groups;

    const courseMap = new Map();
    for (const g of groups) {
      for (const c of g.courseOptions) {
        if (!courseMap.has(c.code)) courseMap.set(c.code, c);
      }
    }
    const depMap = {};
    const codes = Array.from(courseMap.keys());
    await Promise.all(
      codes.map(async (code) => {
        try {
          const pres = await this.getPrerequisites(code);
          depMap[code] = pres.map(p => p.code).filter(pc => courseMap.has(pc));
        } catch {
          depMap[code] = [];
        }
      })
    );
    

    return { program, version, groups, courses: courseMap, depMap };
  }
}