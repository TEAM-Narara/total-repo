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
                dir('Server/SuperBoard/s107-secret-value') {
                    sh 'ls -la'
                }
            }
	        }

        // 백엔드 빌드 단계
        stage('BE-Build') {
            steps {
                dir("./Server/SuperBoard") {
                    // Gradle 빌드 실행
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build'
                }
            }
        }

        // 백엔드 Docker 이미지 빌드 및 컨테이너 실행
        stage('Build BE Docker Image') {
            steps {
                dir("./Server/SuperBoard") {
                    sh 'docker build -t total-server .'
                }
            }
        }

        stage('Remove Existing BE Container') {
            steps {
                // 기존 BE 컨테이너 중지 및 삭제
                sh '''
                if [ $(docker ps -aq -f name=total-server) ]; then
                    docker stop total-server || true
                    docker rm -f total-server
                fi
                '''
            }
        }

        stage('Run New BE Container') {
            steps {
                script {
                    // 네트워크 확인 및 조건 설정
                    def networks = []
                    if (sh(script: 'docker network ls --filter name=^total-server-network$ --format "{{.Name}}"', returnStdout: true).trim()) {
                        networks << "total-server-network"
                    }
                    if (sh(script: 'docker network ls --filter name=^total-server-test-network$ --format "{{.Name}}"', returnStdout: true).trim()) {
                        networks << "total-server-test-network"
                    }

                    // 첫 번째 네트워크에만 연결하여 Docker 컨테이너 실행
                    def primaryNetwork = networks[0]  // 첫 번째 네트워크를 선택
                    sh "docker run -d --name total-server --network ${primaryNetwork} -p 18080:8080 total-server"

                    // 추가 네트워크 연결
                    if (networks.size() > 1) {
                        networks[1..-1].each { network ->
                            // 네트워크 연결 확인 및 디버깅 메시지 출력
                            echo "Connecting to additional network: ${network}"
                            sh "docker network connect ${network} total-server"
                        }
                    }
                }
            }
        }

         stage('SonarQube Analysis') {
            steps {
                dir("./Server/SuperBoard") {
                    script {
                    // 수정
                        def branchName = env.BRANCH_NAME.replaceAll("/", "-")

                        echo "Using branch: ${branchName}"
//                         echo "Using sourceBranch: ${sourceBranch}"

                        sh """
                            ./gradlew --info --warning-mode all sonar \
                            -Dsonar.projectKey=total-server-${branchName} \
                            -Dsonar.projectName=total-server-${branchName}
                        """
                    }
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
