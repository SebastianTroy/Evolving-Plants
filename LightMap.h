#ifndef LIGHTMAP_H
#define LIGHTMAP_H

#include <QColor>
#include <QImage>

#include <vector>

class LightMap {
public:
    struct Colour {
        int red;
        int green;
        int blue;
    };

    LightMap(int64_t width, int64_t height);

    Colour GetLightAt(size_t x, size_t y) const;

    QImage GetLightImage(QRect areaOfInterest) const;
    QRect GetRect() const;

    void AddShadow(int64_t shadowX, int64_t shadowY, int64_t shadowWidth, const QColor& shadowColour);
    void RemoveShadow(int64_t shadowX, int64_t shadowY, int64_t shadowWidth, const QColor& shadowColour);

    LightMap& operator=(const LightMap& other) = delete;
    LightMap& operator=(LightMap&& other) = default;

private:
    int64_t width;
    int64_t height;
    std::vector<std::vector<Colour>> lightData;

    void ModifyData(int64_t shadowX, int64_t shadowY, int64_t areaWidth, int64_t areaHeight, const Colour& values);
};

#endif // LIGHTMAP_H
