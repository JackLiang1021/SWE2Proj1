export class Course {
  constructor(code, name, credits) {
    this.code = code;
    this.name = name;
    this.credits = credits;
    this.prerequisites = [];
  }
  static fromDTO(dto) {
    return new Course(dto.code, dto.name, dto.credits);
  }
}

export class RequirementGroup {
  constructor(id, name, minRequired, courseOptions, programVersionId) {
    this.id = id;
    this.name = name;
    this.minRequired = minRequired;
    this.courseOptions = courseOptions || [];
    this.programVersionId = programVersionId;
  }
  static fromDTO(dto) {
    const pvId = dto.programVersion && dto.programVersion.id;
    return new RequirementGroup(
      dto.id,
      dto.name,
      dto.minRequired,
      (dto.courseOptions || []).map(Course.fromDTO),
      pvId
    );
  }
  totalCredits() {
    return (this.courseOptions || []).reduce((s, c) => s + (c.credits || 0), 0);
  }
}

export class ProgramVersion {
  constructor(id, catalogYear, requiredCredits, groups, programCode, programName) {
    this.id = id;
    this.catalogYear = catalogYear;
    this.requiredCredits = requiredCredits;
    this.groups = groups || [];
    this.programCode = programCode;
    this.programName = programName;
  }
  static fromDTO(dto) {
    const groups = (dto.groups || []).map(RequirementGroup.fromDTO);
    return new ProgramVersion(
      dto.id,
      dto.catalogYear,
      dto.requiredCredits,
      groups,
      dto.program && dto.program.code,
      dto.program && dto.program.name
    );
  }
  allCourses() {
    const seen = new Map();
    for (const g of this.groups) {
      for (const c of g.courseOptions) {
        if (!seen.has(c.code)) seen.set(c.code, c);
      }
    }
    return Array.from(seen.values());
  }
}

export class Program {
  constructor(code, name, versions) {
    this.code = code;
    this.name = name;
    this.versions = versions || [];
  }
  static fromDTO(dto) {
    const versions = (dto.versions || []).map(ProgramVersion.fromDTO);
    return new Program(dto.code, dto.name, versions);
  }
  versionByYear(year) {
    return this.versions.find(v => v.catalogYear === year);
  }
}

export class Student {
  constructor(id, externalId, firstName, lastName, email, programCode, versionId, completedCourses) {
    this.id = id;
    this.externalId = externalId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.programCode = programCode;
    this.versionId = versionId;
    this.completedCourses = completedCourses || [];
  }
  static fromDTO(dto) {
    const completed = (dto.completedCourses || []).map(Course.fromDTO);
    const programCode = dto.program && dto.program.code;
    const versionId = dto.programVersion && dto.programVersion.id;
    return new Student(
      dto.id, dto.externalId, dto.firstName, dto.lastName, dto.email,
      programCode, versionId, completed
    );
  }
}


export function buildDependentsIndex(depMap) {
  const idx = new Map();
  for (const [dependent, prereqs] of Object.entries(depMap)) {
    for (const p of (prereqs || [])) {
      if (!idx.has(p)) idx.set(p, new Set());
      idx.get(p).add(dependent);
    }
  }
  return idx;
}
export function invertDependencyMap(depMap) {
  const inverse = {};
  for (const [dependent, prereqs] of Object.entries(depMap)) {
    for (const p of prereqs || []) {
      if (!inverse[p]) inverse[p] = [];
      inverse[p].push(dependent);
    }
  }
  return inverse;
}