package com.giangnd_svmc.ghalo.entity;


import java.io.Serializable;

/**
 * Created by hoangdd on 2/2/2016.
 */
public class SMS implements Serializable{
    private String _id;
    private String _thread_id;
    private String _address;
    private String person;
    private String date;
    private String date_send;
    private String protocol;
    private String read;
    private String status;
    private String type;
    private String reply_path_present;
    private String subject;
    private String body;
    private String service_center;
    private String locked;
    private String error_code;
    private String seen;

    public SMS() {
    }

    public SMS(String _id, String _thread_id, String _address, String person, String date, String date_send, String protocol, String read, String status, String type, String reply_path_present, String subject, String body, String service_center, String locked, String error_code, String seen) {
        this._id = _id;
        this._thread_id = _thread_id;
        this._address = _address;
        this.person = person;
        this.date = date;
        this.date_send = date_send;
        this.protocol = protocol;
        this.read = read;
        this.status = status;
        this.type = type;
        this.reply_path_present = reply_path_present;
        this.subject = subject;
        this.body = body;
        this.service_center = service_center;
        this.locked = locked;
        this.error_code = error_code;
        this.seen = seen;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_thread_id() {
        return _thread_id;
    }

    public void set_thread_id(String _thread_id) {
        this._thread_id = _thread_id;
    }

    public String get_address() {
        return _address;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_send() {
        return date_send;
    }

    public void setDate_send(String date_send) {
        this.date_send = date_send;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReply_path_present() {
        return reply_path_present;
    }

    public void setReply_path_present(String reply_path_present) {
        this.reply_path_present = reply_path_present;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getService_center() {
        return service_center;
    }

    public void setService_center(String service_center) {
        this.service_center = service_center;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }
}
