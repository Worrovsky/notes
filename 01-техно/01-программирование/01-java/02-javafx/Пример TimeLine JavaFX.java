    public void _start() {

        if (initialized() && !started) {
            started = true;
            Image[] imageArray = new Image[1]; // final variable for lambda
            timeline = new Timeline(new KeyFrame(
                    Duration.millis(delay),
                    event -> {
                        Image currentImage = imageArray[0];
                        if (currentImage == null) {
                            UserFull user = users.get(currentIndex.get());
                            imageArray[0]  = new Image(user.getPhotoMax(), true);
                        } else {
                            if (currentImage.getProgress() >= 0.99) {
                                imageView.setImage(currentImage);
                                UserFull user = users.get(currentIndex.get());
                                System.out.println("set image index " + currentIndex);
                                updateInfo(user);
                                if (currentIndex.get() < users.size()) {
                                    currentIndex.incrementAndGet();
                                    user = users.get(currentIndex.get());
                                    System.out.println("prepare image index " + currentIndex);
                                    imageArray[0] = new Image(user.getPhotoMax(), true);
                                }
                            }
                        }
                 }));
            timeline.setCycleCount(users.size() - currentIndex.get() + 1);
            timeline.play();
        }
    }