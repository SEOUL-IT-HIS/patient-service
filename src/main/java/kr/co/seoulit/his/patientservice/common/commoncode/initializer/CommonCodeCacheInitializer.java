package kr.co.seoulit.his.patientservice.common.commoncode.initializer;

import kr.co.seoulit.his.patientservice.common.commoncode.cache.CommonCodeCache;
import kr.co.seoulit.his.patientservice.common.commoncode.service.CommonCodeCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonCodeCacheInitializer implements ApplicationRunner {

    private final CommonCodeCacheService commonCodeCacheService;
    private final CommonCodeCache commonCodeCache;

    @Override
    public void run(ApplicationArguments args) {
        commonCodeCacheService.loadAllCommonCodes();

        log.info(
                "공통코드 로컬 캐시 적재 완료: 그룹 수={}, 항목 수={}, 그룹={}",
                commonCodeCache.getGroupCount(),
                commonCodeCache.getItemCount(),
                commonCodeCache.getGroupCodes()
        );
    }
}