package Nengcipe.NengcipeBackend.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private Object object;
    public NotFoundException(String field, Object object) {
        super(field+" : 찾을 수 없음");
        this.object=object;
    }
}
