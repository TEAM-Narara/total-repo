pipeline {
    agent any

    stages {
        stage('Git Clone') {
            steps {
                script {
                    // 브랜치 이름 설정
                    def branch = env.gitlabTargetBranch ?: env.gitlabSourceBranch ?: env.GIT_BRANCH?.replaceAll(/^origin\//, '') ?:
                        (env.BRANCH_NAME?.startsWith('refs/heads/') ? env.BRANCH_NAME.replaceAll('refs/heads/', '') : 'BE/deploy')

                    // 디버깅을 위한 로그 추가
                    echo "gitlabTargetBranch: ${env.gitlabTargetBranch}"
                    echo "gitlabSourceBranch: ${env.gitlabSourceBranch}"
                    echo "GIT_BRANCH: ${env.GIT_BRANCH}"
                    echo "BRANCH_NAME: ${env.BRANCH_NAME}"
                    echo "Final branch: ${branch}"
                    echo "Checking out branch: ${branch}"

                    // GitLab에서 코드 클론 (서브모듈 포함)
                    checkout([$class: 'GitSCM',
                        branches: [[name: "*/${branch}"]],  // '*/'를 추가하여 remote 브랜치임을 명시
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
                    sh 'docker build -t total-sever .'
                }
            }
        }

        stage('Remove Existing BE Container') {
            steps {
                // 기존 BE 컨테이너 중지 및 삭제
                sh '''
                if [ $(docker ps -aq -f name=be) ]; then
                    docker stop be || true
                    docker rm -f be
                fi
                '''
            }
        }

        stage('Run New BE Container') {
            steps {
                // 새로운 BE 컨테이너 실행
                sh 'docker run -d --name be --network my-network -p 18080:8080 total-sever'
            }
        }

        // 컨테이너 상태 확인
        stage('Check Containers') {
            steps {
                sh 'docker ps -a'
            }
        }
    }

    post {
        always {
            script {
                sh 'docker ps -a'
            }
        }
    }
}
