declare namespace API {
  type Application = {
    id?: number
    userId?: number
    resumeId?: number
    jobId?: number
    bossId?: number
    status?: number
    appliedAt?: string
    updatedAt?: string
  }

  type ApplicationAddRequest = {
    resumeId?: number
    jobId?: number
    bossId?: number
    status?: number
  }

  type ApplicationQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    userId?: number
    resumeId?: number
    jobId?: number
    bossId?: number
    status?: number
  }

  type ApplicationUpdateRequest = {
    id?: number
    resumeId?: number
    status?: number
  }

  type ApplicationVO = {
    id?: number
    userId?: number
    userName?: string
    resumeId?: number
    jobId?: number
    bossId?: number
    status?: number
    appliedAt?: string
    updatedAt?: string
  }

  type BaseResponseApplication = {
    code?: number
    data?: Application
    message?: string
  }

  type BaseResponseApplicationVO = {
    code?: number
    data?: ApplicationVO
    message?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseBossApplicationDecision = {
    code?: number
    data?: BossApplicationDecision
    message?: string
  }

  type BaseResponseBossApplicationDecisionVO = {
    code?: number
    data?: BossApplicationDecisionVO
    message?: string
  }

  type BaseResponseCompany = {
    code?: number
    data?: Company
    message?: string
  }

  type BaseResponseCompanyVO = {
    code?: number
    data?: CompanyVO
    message?: string
  }

  type BaseResponseInterview = {
    code?: number
    data?: Interview
    message?: string
  }

  type BaseResponseInterviewVO = {
    code?: number
    data?: InterviewVO
    message?: string
  }

  type BaseResponseJobPosting = {
    code?: number
    data?: JobPosting
    message?: string
  }

  type BaseResponseJobPostingVO = {
    code?: number
    data?: JobPostingVO
    message?: string
  }

  type BaseResponseListUserVO = {
    code?: number
    data?: UserVO[]
    message?: string
  }

  type BaseResponseLoginUserVO = {
    code?: number
    data?: LoginUserVO
    message?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageApplicationVO = {
    code?: number
    data?: PageApplicationVO
    message?: string
  }

  type BaseResponsePageBossApplicationDecisionVO = {
    code?: number
    data?: PageBossApplicationDecisionVO
    message?: string
  }

  type BaseResponsePageCompanyVO = {
    code?: number
    data?: PageCompanyVO
    message?: string
  }

  type BaseResponsePageInterviewVO = {
    code?: number
    data?: PageInterviewVO
    message?: string
  }

  type BaseResponsePageJobPostingVO = {
    code?: number
    data?: PageJobPostingVO
    message?: string
  }

  type BaseResponsePageResumeVO = {
    code?: number
    data?: PageResumeVO
    message?: string
  }

  type BaseResponsePageUserVO = {
    code?: number
    data?: PageUserVO
    message?: string
  }

  type BaseResponseResumeVO = {
    code?: number
    data?: ResumeVO
    message?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUser = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO = {
    code?: number
    data?: UserVO
    message?: string
  }

  type BossApplicationDecision = {
    id?: number
    applicationId?: number
    bossId?: number
    decision?: number
    stage?: number
    note?: string
    decidedAt?: string
    updatedAt?: string
  }

  type BossApplicationDecisionAddRequest = {
    applicationId?: number
    bossId?: number
    decision?: number
    stage?: number
    note?: string
    decidedAt?: string
  }

  type BossApplicationDecisionQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    applicationId?: number
    bossId?: number
    decision?: number
    stage?: number
  }

  type BossApplicationDecisionUpdateRequest = {
    id?: number
    decision?: number
    stage?: number
    note?: string
    decidedAt?: string
  }

  type BossApplicationDecisionVO = {
    id?: number
    applicationId?: number
    bossId?: number
    decision?: number
    stage?: number
    note?: string
    decidedAt?: string
    updatedAt?: string
  }

  type Company = {
    id?: number
    name?: string
    industry?: string
    sizeRange?: string
    website?: string
    address?: string
    intro?: string
    status?: number
    createdAt?: string
    updatedAt?: string
  }

  type CompanyAddRequest = {
    name?: string
    industry?: string
    sizeRange?: string
    website?: string
    address?: string
    intro?: string
    status?: number
  }

  type CompanyQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    name?: string
    industry?: string
    status?: number
  }

  type CompanyUpdateRequest = {
    id?: number
    name?: string
    industry?: string
    sizeRange?: string
    website?: string
    address?: string
    intro?: string
    status?: number
  }

  type CompanyVO = {
    id?: number
    name?: string
    industry?: string
    sizeRange?: string
    website?: string
    address?: string
    intro?: string
    status?: number
    createdAt?: string
    updatedAt?: string
  }

  type DeleteRequest = {
    id?: number
  }

  type getApplicationByIdParams = {
    id: number
  }

  type getApplicationVOByIdParams = {
    id: number
  }

  type getCompanyByIdParams = {
    id: number
  }

  type getCompanyVOByIdParams = {
    id: number
  }

  type getDecisionByIdParams = {
    id: number
  }

  type getDecisionVOByIdParams = {
    id: number
  }

  type getInterviewByIdParams = {
    id: number
  }

  type getInterviewVOByIdParams = {
    id: number
  }

  type getJobByIdParams = {
    id: number
  }

  type getJobVOByIdParams = {
    id: number
  }

  type getResumeVOByIdParams = {
    id: number
  }

  type getUserByIdParams = {
    id: number
  }

  type getUserVOByIdParams = {
    id: number
  }

  type Interview = {
    id?: number
    applicationId?: number
    bossId?: number
    userId?: number
    interviewTime?: string
    mode?: number
    location?: string
    status?: number
    note?: string
    createdAt?: string
    updatedAt?: string
  }

  type InterviewAddRequest = {
    applicationId?: number
    bossId?: number
    userId?: number
    interviewTime?: string
    mode?: number
    location?: string
    status?: number
    note?: string
  }

  type InterviewQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    applicationId?: number
    bossId?: number
    userId?: number
    status?: number
  }

  type InterviewUpdateRequest = {
    id?: number
    interviewTime?: string
    mode?: number
    location?: string
    status?: number
    note?: string
  }

  type InterviewVO = {
    id?: number
    applicationId?: number
    bossId?: number
    userId?: number
    interviewTime?: string
    mode?: number
    location?: string
    status?: number
    note?: string
    createdAt?: string
    updatedAt?: string
  }

  type JobPosting = {
    id?: number
    bossId?: number
    companyId?: number
    title?: string
    location?: string
    jobType?: string
    salaryMin?: number
    salaryMax?: number
    description?: string
    requirement?: string
    status?: number
    publishAt?: string
    createdAt?: string
    updatedAt?: string
  }

  type JobPostingAddRequest = {
    bossId?: number
    companyId?: number
    title?: string
    location?: string
    jobType?: string
    salaryMin?: number
    salaryMax?: number
    description?: string
    requirement?: string
    status?: number
  }

  type JobPostingQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    bossId?: number
    companyId?: number
    title?: string
    location?: string
    jobType?: string
    status?: number
  }

  type JobPostingUpdateRequest = {
    id?: number
    title?: string
    location?: string
    jobType?: string
    salaryMin?: number
    salaryMax?: number
    description?: string
    requirement?: string
    status?: number
  }

  type JobPostingVO = {
    id?: number
    bossId?: number
    companyId?: number
    title?: string
    location?: string
    jobType?: string
    salaryMin?: number
    salaryMax?: number
    description?: string
    requirement?: string
    status?: number
    publishAt?: string
    createdAt?: string
    updatedAt?: string
  }

  type LoginUserVO = {
    id?: number
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    companyId?: number
    editTime?: string
    createdAt?: string
    updatedAt?: string
  }

  type OrderItem = {
    column?: string
    asc?: boolean
  }

  type PageApplicationVO = {
    records?: ApplicationVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageApplicationVO
    searchCount?: PageApplicationVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageBossApplicationDecisionVO = {
    records?: BossApplicationDecisionVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageBossApplicationDecisionVO
    searchCount?: PageBossApplicationDecisionVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageCompanyVO = {
    records?: CompanyVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageCompanyVO
    searchCount?: PageCompanyVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageInterviewVO = {
    records?: InterviewVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageInterviewVO
    searchCount?: PageInterviewVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageJobPostingVO = {
    records?: JobPostingVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageJobPostingVO
    searchCount?: PageJobPostingVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageResumeVO = {
    records?: ResumeVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageResumeVO
    searchCount?: PageResumeVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type PageUserVO = {
    records?: UserVO[]
    total?: number
    size?: number
    current?: number
    orders?: OrderItem[]
    optimizeCountSql?: PageUserVO
    searchCount?: PageUserVO
    optimizeJoinOfCountSql?: boolean
    maxLimit?: number
    countId?: string
    pages?: number
  }

  type ResumeAddRequest = {
    resumeTitle?: string
    isDefault?: number
    summary?: string
    content?: string
    attachmentUrl?: string
  }

  type ResumeQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    userId?: number
    resumeTitle?: string
    isDefault?: number
  }

  type ResumeUpdateRequest = {
    id?: number
    resumeTitle?: string
    isDefault?: number
    summary?: string
    content?: string
    attachmentUrl?: string
  }

  type ResumeVO = {
    id?: number
    userId?: number
    resumeTitle?: string
    isDefault?: number
    summary?: string
    content?: string
    attachmentUrl?: string
    updatedAt?: string
    createdAt?: string
  }

  type User = {
    id?: number
    userAccount?: string
    userPassword?: string
    phone?: string
    email?: string
    userName?: string
    userRole?: string
    companyId?: number
    status?: number
    createdAt?: string
    updatedAt?: string
    isDelete?: number
    deletedAt?: string
    userProfile?: string
  }

  type UserLoginRequest = {
    usrAccount?: string
    userPassword?: string
  }

  type UserProfileUpdateRequest = {
    id?: number
    oldPassword?: string
    userPassword?: string
    email?: string
    phone?: string
    userProfile?: string
  }

  type UserQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    userAccount?: string
    userName?: string
    userProfile?: string
  }

  type UserRegisterRequest = {
    usrAccount?: string
    userPassword?: string
    checkPassword?: string
    userRole?: string
  }

  type UserUpdateRequest = {
    id?: number
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    id?: number
    userAccount?: string
    phone?: string
    email?: string
    userName?: string
    status?: number
    userRole?: string
    companyId?: number
    userProfile?: string
    createdAt?: string
    updatedAt?: string
  }
}
