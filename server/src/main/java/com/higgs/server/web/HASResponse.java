package com.higgs.server.web;

import com.higgs.server.util.JsonUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HASResponse<T> {
    private T response;

    private HttpStatus status;

    private String error;

    public static <K> Builder<K> builder(final K response) {
        return new Builder<>(response);
    }

    public ResponseEntity<String> toResponseEntity() {
        return ResponseEntity.status(Optional.ofNullable(this.status).orElse(HttpStatus.OK)).body(this.toString());
    }

    @Override
    public String toString() {
        return JsonUtils.getDefaultGson().toJson(this);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder<V> {
        private V response;

        private HttpStatus status;

        private String error;

        public Builder(final V response) {
            this.response = response;
        }

        public Builder<V> status(final HttpStatus status) {
            this.status = status;
            return this;
        }

        public Builder<V> error(final String error) {
            this.error = error;
            return this;
        }

        public HASResponse<V> build() {
            return new HASResponse<>(this.response, this.status, this.error);
        }
    }
}
