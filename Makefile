# 콘솔창 명령어 : make local-up

# [f] 어떠한 docker-compose 파일을 실행 시킬지 명시
# [up] 컨테이너 생성 이후 실행
# [d] 백그라운드 실행
# [force--recreate] 컨테이너를 지우고 생성
# [--build] 이미지가 있든 없든 재빌드 하여 컨테이너 실행
local-up:
	docker-compose -f docker-compose-local.yml up -d --force-recreate --build

# [down] 컨테이너 정지 이후 삭제
# [v] 볼륨까지 삭제
local-down:
	docker-compose -f docker-compose-local.yml down -v

prod-up:
	docker-compose -f docker-compose.yml up -d --force-recreate --build

# [down] 컨테이너 정지 이후 삭제
# [v] 볼륨까지 삭제
prod-down:
	docker-compose down -v




oracle-up:
	docker-compose -f docker-compose-oracle.yml up -d --force-recreate --build

# [down] 컨테이너 정지 이후 삭제
# [v] 볼륨까지 삭제
oracle-down:
	docker-compose -f docker-compose-oracle.yml down -v