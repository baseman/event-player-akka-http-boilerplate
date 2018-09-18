package my.artifact.myeventplayer.api.services

import org.springframework.stereotype.Service

@Service
class MyService {
    fun something(x: Int?): Int? {
        return x!! * x
    }
}
