package com.swapper.monolith.exception.enums;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal=true)
public enum ApiResponses {
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
    DUPLICATED_RESOURCE(HttpStatus.CONFLICT,"This Resource Already Exists"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource Not Found"),
    CREATED(HttpStatus.CREATED, "Created Successfully"),
    OK(HttpStatus.OK, "Retrieved Successfully"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh token has expired, please log in again"),
    REFRESH_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid refresh token"),
    PASSWORD_RESET_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Password reset link has expired, please request a new one"),
    PASSWORD_RESET_TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "Invalid password reset token"),
    LISTING_ACTIVE_TRANSACTION(HttpStatus.CONFLICT, "Cannot delete a listing with an active transaction in progress"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "You do not have permission to perform this action"),
    WISHLIST_GAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "This game is already in your wishlist"),
    TRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "Trade not found"),
    RENTAL_NOT_FOUND(HttpStatus.NOT_FOUND, "Rental not found"),
    TRADE_LISTING_NOT_TRADEABLE(HttpStatus.BAD_REQUEST, "Listing is not available for trading"),
    RENTAL_LISTING_NOT_RENTABLE(HttpStatus.BAD_REQUEST, "Listing is not available for renting"),
    TRADE_DUPLICATE(HttpStatus.CONFLICT, "A pending trade already exists for these listings"),
    TRADE_CANNOT_TRADE_OWN_LISTING(HttpStatus.BAD_REQUEST, "You cannot trade with yourself"),
    TRADE_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "Trade cannot be updated in its current state"),
    RENTAL_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "Rental cannot be updated in its current state"),
    CHAT_CONVERSATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Conversation not found"),
    CHAT_NOT_A_PARTICIPANT(HttpStatus.FORBIDDEN, "You are not a participant in this conversation"),
    CHAT_NO_ACCEPTED_TRADE(HttpStatus.FORBIDDEN, "Chat is only available after a trade is accepted between you and the other user"),
    CHAT_COUNTERPARTY_NOT_FOUND(HttpStatus.NOT_FOUND, "Counterparty user not found"),
    CHAT_INVALID_MESSAGE_TYPE_FIELDS(HttpStatus.BAD_REQUEST, "Message fields do not match the declared message type"),
    CHAT_MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Message not found"),
    CHAT_PRESET_NOT_FOUND(HttpStatus.NOT_FOUND, "Preset message not found"),
    CHAT_PRESET_INACTIVE(HttpStatus.CONFLICT, "Preset message is not active"),
    ;
    HttpStatus httpStatus;
    String message;

    ApiResponses(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
    public String getMessage() {
        return this.message;
    }
}
