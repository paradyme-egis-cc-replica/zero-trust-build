String applicationGithubCredId = 'github-id'
String orgName = 'paradyme-egis-cc-replica'
String source_repository = "zero-trust-c"
String jteRepository = "jenkins-template-engine"
String jteUrl = "https://github.com/" + orgName + "/" + jteRepository
String jenkinsTemplateLibraryUrl = jteUrl + ".git"

multibranchPipelineJob(source_repository) {
    properties {
        templateConfigFolderProperty {
            tier {
                configurationProvider {
                    scmPipelineConfigurationProvider {
                        baseDir('infrastructure')
                        scm {
                            gitSCM {
                                userRemoteConfigs {
                                    userRemoteConfig {
                                        name('origin')
                                        refspec('+refs/heads/*:refs/remotes/origin/*')
                                        url(jenkinsTemplateLibraryUrl)
                                        credentialsId(applicationGithubCredId)
                                    }
                                }
                                branches {
                                    branchSpec {
                                        name ('*/main')
                                    }
                                }
                                browser {
                                    githubWeb {
                                        repoUrl(jteRepository)
                                    }
                                }
                                gitTool('/usr/bin/git')
                            }
                        }
                    }
                }
                librarySources {
                    librarySource {
                        libraryProvider {
                            scmLibraryProvider {
                                baseDir('libraries')
                                scm {
                                    gitSCM {
                                        userRemoteConfigs {
                                            userRemoteConfig {
                                                name('origin')
                                                refspec('+refs/heads/*:refs/remotes/origin/*')
                                                url(jenkinsTemplateLibraryUrl)
                                                credentialsId(applicationGithubCredId)
                                            }
                                        }
                                        browser {
                                            githubWeb {
                                                repoUrl(jteRepository)
                                            }
                                        }
                                        branches {
                                            branchSpec {
                                                name ('*/main')
                                            }
                                        }
                                        gitTool('/usr/bin/git')
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    branchSources {
        github {
            id(UUID.randomUUID().toString())
            scanCredentialsId(applicationGithubCredId)
            checkoutCredentialsId(applicationGithubCredId)
            repoOwner(orgName)
            repository(source_repository)
            buildOriginBranch(true)
            buildOriginBranchWithPR(true)
            buildOriginPRHead(false)
            buildOriginPRMerge(true)
        }
    }
    configure {
        it / 'projectFactories' << 'org.boozallen.plugins.jte.job.TemplateMultiBranchProjectFactory' {
        }
        it / factory(class: 'org.boozallen.plugins.jte.job.TemplateBranchProjectFactory') {
            filterBranches(true)
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            numToKeep(3)
            daysToKeep(3)
        }
    }
    factory {
        templateBranchProjectFactory {
            configurationPath('pipeline_config.groovy')
            scriptPath('Jenkinsfile')
            filterBranches(true)
        }
    }
}
