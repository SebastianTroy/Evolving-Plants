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

    LightMap(size_t width, size_t height);

    Colour GetLightMinusShadowAt(size_t x, size_t y, QColor shadowColor) const;

    // FIXME update to take x & width parameters
    QImage GetLightImage() const;

    void AddShadow(size_t shadowX, size_t shadowY, size_t shadowWidth, const QColor& shadowColour);
    void RemoveShadow(size_t shadowX, size_t shadowY, size_t shadowWidth, const QColor& shadowColour);

    LightMap& operator=(const LightMap& other) = delete;
    LightMap& operator=(LightMap&& other) = default;

private:
    size_t width;
    size_t height;
    std::vector<std::vector<Colour>> lightData;
};

#endif // LIGHTMAP_H
