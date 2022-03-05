#include "LightMap.h"

LightMap::LightMap(int64_t width, int64_t height)
    : width(std::max(int64_t{ 0 }, width))
    , height(std::max(int64_t{ 0 }, height))
    , lightData(this->width, std::vector<Colour>(this->height, Colour{ 255, 255, 255 }))
{
}

LightMap::Colour LightMap::GetLightAt(size_t x, size_t y) const
{
    Colour light{ 0, 0, 0 };
    if (GetRect().contains(x, y)) {
        light.red = lightData[x][y].red;
        light.green = lightData[x][y].green;
        light.blue = lightData[x][y].blue;
    }
    return light;
}

QImage LightMap::GetLightImage(QRect areaOfInterest) const
{
    QImage lightMap(areaOfInterest.width(), areaOfInterest.height(), QImage::Format::Format_RGB32);
    lightMap.fill(Qt::black);

    for (int x = areaOfInterest.left(); x < areaOfInterest.right(); ++x) {
        for (int y = areaOfInterest.top(); y < areaOfInterest.bottom(); ++y) {
            if (GetRect().contains(x, y)) {
                int red = std::clamp(lightData[x][y].red, 0, 255);
                int green = std::clamp(lightData[x][y].green, 0, 255);
                int blue = std::clamp(lightData[x][y].blue, 0, 255);

                int pixelX = x - areaOfInterest.left();
                int pixelY = y - areaOfInterest.top();
                lightMap.setPixelColor(pixelX, pixelY, QColor::fromRgb(red, green, blue));
            }
        }
    }
    return lightMap;
}

QRect LightMap::GetRect() const
{
    return QRect(0, 0, width, height);
}

void LightMap::AddShadow(int64_t shadowX, int64_t shadowY, int64_t shadowWidth, const QColor& shadowColour)
{
    ModifyData(shadowX, 0, shadowWidth, shadowY + 1, Colour{ -shadowColour.red(), -shadowColour.green(), -shadowColour.blue() });
}

void LightMap::RemoveShadow(int64_t shadowX, int64_t shadowY, int64_t shadowWidth, const QColor& shadowColour)
{
    ModifyData(shadowX, 0, shadowWidth, shadowY + 1, Colour{ shadowColour.red(), shadowColour.green(), shadowColour.blue() });
}

void LightMap::ModifyData(int64_t startX, int64_t startY, int64_t areaWidth, int64_t areaHeight, const Colour& values)
{
    int64_t endX = startX + areaWidth;
    int64_t endY = startY + areaHeight;

    startX = std::clamp(startX, int64_t{ 0 }, width);
    startY = std::clamp(startY, int64_t{ 0 }, height);
    endX = std::clamp(endX, int64_t{ 0 }, width);
    endY = std::clamp(endY, int64_t{ 0 }, height);

    for (int64_t x = startX; x < endX; ++x) {
        for (int64_t y = startY; y < endY; ++y) {
            lightData[x][y].red += values.red;
            lightData[x][y].green += values.green;
            lightData[x][y].blue += values.blue;
        }
    }
}
