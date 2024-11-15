pipeline {
    agent any

    // 젠킨스 설정의 tool에서 설정해줘야함.
    tools {
        jdk ("jdk21")
    }
    
    stages {
        stage('Git Clone') {
            steps {
                script {
                   // 전역 환경 변수에 브랜치 이름 설정
                   env.BRANCH_NAME = env.GIT_BRANCH ? env.GIT_BRANCH.replaceAll(/^origin\//, '') : 'BE/deploy'
                   echo "Final branch: ${env.BRANCH_NAME}"
                   echo "Checking out branch: ${env.BRANCH_NAME}"

                    // 브랜치 이름 설정
//                     def branch = env.GIT_BRANCH ? env.GIT_BRANCH.replaceAll(/^origin\//, '') : 'BE/deploy'
//                     def branchName  = env.gitlabTargetBranch ?: env.gitlabSourceBranch ?: env.GIT_BRANCH?.replaceAll(/^origin\//, '') ?:
//                         (env.BRANCH_NAME?.startsWith('refs/heads/') ? env.BRANCH_NAME.replaceAll('refs/heads/', '') : 'BE/deploy')
//                     env.BRANCH_NAME = branch


                    // GitLab에서 코드 클론 (서브모듈 포함)
                    checkout([$class: 'GitSCM',
                        branches: [[name: "*/${env.BRANCH_NAME}"]],
                        extensions: [[
                            $class: 'SubmoduleOption',
                            parentCredentials: true,
                            recursiveSubmodules: true,
                            trackingSubmodules: true
                        ]],
                        userRemoteConfigs: [[
                            credentialsId: 'gitlabId',
                            url: 'https://lab.ssafy.com/s11-final/S11P31S107.git'
                        ]]
                    ])
                }
            }
        }

        stage('Verify Submodule') {
            steps {
                // 서브모듈 디렉토리의 파일이 잘 클론되었는지 확인
                dir('Server/config/s107-secret-value') {
                    sh 'ls -la'
                }
            }
	        }

        // 백엔드 빌드 단계
        stage('BE-Build') {
            steps {
                dir("./Server/config") {
                    // Gradle 빌드 실행
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build'
                }
            }
        }

        // 백엔드 Docker 이미지 빌드 및 컨테이너 실행
        stage('Build BE Docker Image') {
            steps {
                dir("./Server/config") {
                    sh 'docker build -t config-server .'
                }
            }
        }

        stage('Remove Existing BE Container') {
            steps {
                // 기존 BE 컨테이너 중지 및 삭제
                sh '''
                if [ $(docker ps -aq -f name=config-server) ]; then
                    docker stop config-server || true
                    docker rm -f config-server
                fi
                '''
            }
        }

        stage('Run New BE Container') {
            steps {
                script {
                    sh "docker run -d --name config-server -p 19000:19000 config-server"
                }
            }
        }

         

        // 컨테이너 상태 확인
        stage('Check Containers') {
            steps {
                sh 'docker ps -a'
            }
        }
    }
}
