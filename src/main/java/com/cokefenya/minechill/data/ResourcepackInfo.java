package com.cokefenya.minechill.data;


public class ResourcepackInfo {
    public ResourcepackInfo() {
    }

    public static class PackInfo {
        public String name;
        public String senderName;
        public String url;
        public String description;

        public PackInfo(String name, String senderName, String url, String description) {
            this.name = name;
            this.senderName = senderName;
            this.url = url;
            this.description = description;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSenderName() {
            return this.senderName;
        }

        public void setSenderName(String senderName) {
            this.senderName = senderName;
        }

        public String getUrl() {
            return this.url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
